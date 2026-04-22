package com.santiagocz.membershipservice.services;

import com.santiagocz.membershipservice.domain.entities.Membership;
import com.santiagocz.membershipservice.domain.enums.MembershipStatus;
import com.santiagocz.membershipservice.dto.MembershipRequestDto;
import com.santiagocz.membershipservice.dto.MembershipResponseDto;
import com.santiagocz.membershipservice.repositories.MembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;

    @Transactional(readOnly = true)
    public List<MembershipResponseDto> findAll() {
        return membershipRepository.findAll()
                .stream()
                .map(this::buildResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MembershipResponseDto> findByStatus(MembershipStatus status) {
        return membershipRepository.findByStatus(status)
                .stream()
                .map(this::buildResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MembershipResponseDto findById(Long id) {
        Membership membership = membershipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Membership not found with id: " + id));
        return buildResponseDto(membership);
    }

    @Transactional
    public MembershipResponseDto create(MembershipRequestDto dto) {
        validateNameNotTaken(dto.getName());
        Membership membership = buildEntity(dto);
        membership.setStatus(MembershipStatus.ACTIVE);
        return buildResponseDto(membershipRepository.save(membership));
    }

    @Transactional
    public MembershipResponseDto update(Long id, MembershipRequestDto dto) {
        Membership membership = membershipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Membership not found with id: " + id));

        if (membership.getStatus() == MembershipStatus.INACTIVE) {
            throw new RuntimeException("Cannot update an inactive membership");
        }

        if (!membership.getName().equals(dto.getName())) {
            validateNameNotTaken(dto.getName());
        }

        membership.setName(dto.getName());
        membership.setDescription(dto.getDescription());
        membership.setDurationDays(dto.getDurationDays());
        membership.setWeeklyLimit(dto.getWeeklyLimit());
        membership.setPrice(dto.getPrice());

        return buildResponseDto(membershipRepository.save(membership));
    }

    private void validateNameNotTaken(String name) {
        membershipRepository.findByName(name).ifPresent(m -> {
            if (m.getStatus() == MembershipStatus.ACTIVE) {
                throw new RuntimeException("Membership already exists with name: " + name);
            }
        });
    }

    @Transactional
    public void delete(Long id) {
        Membership membership = membershipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Membership not found with id: " + id));

        if (membership.getStatus() == MembershipStatus.INACTIVE) {
            throw new RuntimeException("Membership is already inactive");
        }

        membership.setStatus(MembershipStatus.INACTIVE);
        membershipRepository.save(membership);
    }

    // Mappers
    private MembershipResponseDto buildResponseDto(Membership membership) {
        return MembershipResponseDto.builder()
                .id(membership.getId())
                .name(membership.getName())
                .description(membership.getDescription())
                .durationDays(membership.getDurationDays())
                .weeklyLimit(membership.getWeeklyLimit())
                .price(membership.getPrice())
                .status(membership.getStatus())
                .build();
    }

    private Membership buildEntity(MembershipRequestDto dto) {
        return Membership.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .durationDays(dto.getDurationDays())
                .weeklyLimit(dto.getWeeklyLimit())
                .price(dto.getPrice())
                .build();
    }
}
package com.santiagocz.clientservice.services;

import com.santiagocz.clientservice.dto.MemberRequestDto;
import com.santiagocz.clientservice.dto.MemberResponseDto;
import com.santiagocz.clientservice.domain.entities.Member;
import com.santiagocz.clientservice.domain.enums.MemberStatus;
import com.santiagocz.clientservice.repositories.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<MemberResponseDto> findAll() {
        return memberRepository.findAll()
                .stream()
                .map(this::buildResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MemberResponseDto> findByStatus(MemberStatus status) {
        return memberRepository.findByStatus(status)
                .stream()
                .map(this::buildResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MemberResponseDto findById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
        return buildResponseDto(member);
    }

    @Transactional(readOnly = true)
    public MemberResponseDto findByDni(String dni) {
        Member member = memberRepository.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Member not found with DNI: " + dni));
        return buildResponseDto(member);
    }

    @Transactional
    public MemberResponseDto create(MemberRequestDto dto) {
        if (memberRepository.existsByDni(dto.getDni())) {
            throw new RuntimeException("Member already exists with DNI: " + dto.getDni());
        }
        Member member = buildEntity(dto);
        return buildResponseDto(memberRepository.save(member));
    }

    @Transactional
    public MemberResponseDto update(Long id, MemberRequestDto dto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));

        if (!member.getDni().equals(dto.getDni()) && memberRepository.existsByDni(dto.getDni())) {
            throw new RuntimeException("Member already exists with DNI: " + dto.getDni());
        }

        member.setDni(dto.getDni());
        member.setFirstName(dto.getFirstName());
        member.setLastName(dto.getLastName());
        member.setEmail(dto.getEmail());
        member.setPhone(dto.getPhone());
        member.setBirthDate(dto.getBirthDate());
        member.setRegistrationDate(dto.getRegistrationDate());
        member.setStatus(dto.getStatus());

        return buildResponseDto(memberRepository.save(member));
    }

    @Transactional
    public void delete(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
        member.setStatus(MemberStatus.INACTIVE);
        memberRepository.save(member);
    }

    // Mappers
    private MemberResponseDto buildResponseDto(Member member) {
        return MemberResponseDto.builder()
                .id(member.getId())
                .dni(member.getDni())
                .firstName(member.getFirstName())
                .lastName(member.getLastName())
                .email(member.getEmail())
                .phone(member.getPhone())
                .birthDate(member.getBirthDate())
                .registrationDate(member.getRegistrationDate())
                .status(member.getStatus())
                .build();
    }

    private Member buildEntity(MemberRequestDto dto) {
        return Member.builder()
                .dni(dto.getDni())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .birthDate(dto.getBirthDate())
                .registrationDate(dto.getRegistrationDate())
                .status(dto.getStatus())
                .build();
    }
}

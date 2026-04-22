package com.santiagocz.membershipservice.services;

import com.santiagocz.membershipservice.domain.entities.Membership;
import com.santiagocz.membershipservice.domain.entities.Subscription;
import com.santiagocz.membershipservice.domain.enums.SubscriptionStatus;
import com.santiagocz.membershipservice.dto.MembershipResponseDto;
import com.santiagocz.membershipservice.dto.SubscriptionRequestDto;
import com.santiagocz.membershipservice.dto.SubscriptionResponseDto;
import com.santiagocz.membershipservice.repositories.MembershipRepository;
import com.santiagocz.membershipservice.repositories.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final MembershipRepository membershipRepository;

    @Transactional(readOnly = true)
    public List<SubscriptionResponseDto> findByMemberId(Long memberId) {
        return subscriptionRepository.findByMemberId(memberId)
                .stream()
                .map(this::buildResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SubscriptionResponseDto> findByStatus(SubscriptionStatus status) {
        return subscriptionRepository.findByStatus(status)
                .stream()
                .map(this::buildResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SubscriptionResponseDto findActiveByMemberId(Long memberId) {
        Subscription subscription = subscriptionRepository
                .findByMemberIdAndStatus(memberId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("No active subscription found for member: " + memberId));
        return buildResponseDto(subscription);
    }

    @Transactional
    public SubscriptionResponseDto create(SubscriptionRequestDto dto) {

        subscriptionRepository.findByMemberIdAndStatus(dto.getMemberId(), SubscriptionStatus.ACTIVE)
                .ifPresent(s -> {
                    throw new RuntimeException("Member already has an active subscription");
                });

        Membership membership = membershipRepository.findById(dto.getMembershipId())
                .orElseThrow(() -> new RuntimeException("Membership not found with id: " + dto.getMembershipId()));

        Subscription subscription = Subscription.builder()
                .memberId(dto.getMemberId())
                .membership(membership)
                .startDate(dto.getStartDate())
                .endDate(dto.getStartDate().plusDays(membership.getDurationDays()))
                .status(SubscriptionStatus.ACTIVE)
                .build();

        return buildResponseDto(subscriptionRepository.save(subscription));
    }

    @Transactional
    public SubscriptionResponseDto cancel(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found with id: " + id));

        if (subscription.getStatus() == SubscriptionStatus.CANCELLED) {
            throw new RuntimeException("Subscription is already cancelled");
        }

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        return buildResponseDto(subscriptionRepository.save(subscription));
    }

    @Transactional
    public void expireSubscriptions() {
        List<Subscription> expired = subscriptionRepository
                .findByEndDateBeforeAndStatus(LocalDate.now(), SubscriptionStatus.ACTIVE);

        expired.forEach(s -> s.setStatus(SubscriptionStatus.EXPIRED));
        subscriptionRepository.saveAll(expired);
    }

    // Mappers
    private SubscriptionResponseDto buildResponseDto(Subscription subscription) {
        return SubscriptionResponseDto.builder()
                .id(subscription.getId())
                .memberId(subscription.getMemberId())
                .membership(buildMembershipResponseDto(subscription.getMembership()))
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .status(subscription.getStatus())
                .build();
    }

    private MembershipResponseDto buildMembershipResponseDto(Membership membership) {
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
}

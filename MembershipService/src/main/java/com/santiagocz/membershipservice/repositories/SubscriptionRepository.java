package com.santiagocz.membershipservice.repositories;

import com.santiagocz.membershipservice.domain.entities.Subscription;
import com.santiagocz.membershipservice.domain.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByMemberId(Long memberId);

    List<Subscription> findByStatus(SubscriptionStatus status);

    Optional<Subscription> findByMemberIdAndStatus(Long memberId, SubscriptionStatus status);

    List<Subscription> findByEndDateBeforeAndStatus(LocalDate date, SubscriptionStatus status);
}
package com.santiagocz.paymentservice.repositories;

import com.santiagocz.paymentservice.domain.entities.Payment;
import com.santiagocz.paymentservice.domain.enums.PaymentMethod;
import com.santiagocz.paymentservice.domain.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByMemberId(Long memberId);

    List<Payment> findByMembershipId(Long subscriptionId);

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByPaymentDateBetween(
            LocalDateTime from,
            LocalDateTime to);

    List<Payment> findByMemberIdAndPaymentDateBetween(
            Long memberId,
            LocalDateTime from,
            LocalDateTime to);
}
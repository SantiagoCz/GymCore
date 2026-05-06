package com.santiagocz.paymentservice.services;

import com.santiagocz.paymentservice.clients.MemberClient;
import com.santiagocz.paymentservice.clients.MembershipClient;
import com.santiagocz.paymentservice.domain.entities.Payment;
import com.santiagocz.paymentservice.domain.entities.Promotion;
import com.santiagocz.paymentservice.domain.enums.DiscountType;
import com.santiagocz.paymentservice.domain.enums.PaymentStatus;
import com.santiagocz.paymentservice.domain.enums.PromotionCondition;
import com.santiagocz.paymentservice.dto.*;
import com.santiagocz.paymentservice.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PromotionService promotionService;
    private final MemberClient memberClient;
    private final MembershipClient membershipClient;

    @Transactional(readOnly = true)
    public List<PaymentResponseDto> findByMemberId(Long memberId) {
        return paymentRepository.findByMemberId(memberId)
                .stream()
                .map(this::buildResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDto> findByMembershipId(Long membershipId) {
        return paymentRepository.findByMembershipId(membershipId)
                .stream()
                .map(this::buildResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDto> findByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status)
                .stream()
                .map(this::buildResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDto> findByDateRange(LocalDateTime from, LocalDateTime to) {
        return paymentRepository.findByPaymentDateBetween(from, to)
                .stream()
                .map(this::buildResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDto> findByMemberIdAndDateRange(Long memberId, LocalDateTime from, LocalDateTime to) {
        MemberDto member = validateMember(memberId);
        return paymentRepository.findByMemberIdAndPaymentDateBetween(member.getId(), from, to)
                .stream()
                .map(this::buildResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PaymentResponseDto create(PaymentRequestDto dto) {

        MemberDto member = validateMember(dto.getMemberId());

        MembershipDto membership = getMembership(dto.getMembershipId());
        BigDecimal originalAmount = membership.getPrice();

        BigDecimal discountAmount = calculateDiscount(dto, member, originalAmount);
        String discountReason = resolveDiscountReason(dto, member);
        BigDecimal finalAmount = originalAmount.subtract(discountAmount).max(BigDecimal.ZERO);

        Payment payment = Payment.builder()
                .memberId(dto.getMemberId())
                .membershipId(dto.getMembershipId())
                .paymentMethod(dto.getPaymentMethod())
                .originalAmount(originalAmount)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .discountReason(discountReason)
                .voucher(dto.getVoucher())
                .notes(dto.getNotes())
                .status(PaymentStatus.COMPLETED)
                .paymentDate(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

        membershipClient.renew(dto.getMemberId(), dto.getMembershipId());

        return buildResponseDto(payment);
    }

    @Transactional
    public PaymentResponseDto refund(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new RuntimeException("Payment is already refunded");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        return buildResponseDto(paymentRepository.save(payment));
    }

    // Helpers
    private MembershipDto getMembership(Long membershipId) {
        try {
            return membershipClient.getMembershipById(membershipId);
        } catch (Exception e) {
            throw new RuntimeException("Membership not found with id: " + membershipId);
        }
    }

    private MemberDto validateMember(Long memberId) {
        try {
            MemberDto member = memberClient.getMemberById(memberId);
            if (!"ACTIVE".equals(member.getStatus())) {
                throw new RuntimeException("Member is not active");
            }
            return member;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Member not found with id: " + memberId);
        }
    }

    private BigDecimal calculateDiscount(PaymentRequestDto dto, MemberDto member, BigDecimal originalAmount) {

        // If there is a manual discount, it takes priority.
        if (dto.getManualDiscountAmount() != null &&
                dto.getManualDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            return dto.getManualDiscountAmount();
        }

        // Detect automatic promotion
        PromotionCondition condition = isNewMember(member)
                ? PromotionCondition.NEW_MEMBER
                : PromotionCondition.ALL_MEMBERS;

        Optional<Promotion> promo = promotionService.findActivePromotion(condition);

        if (promo.isEmpty()) {
            promo = promotionService.findActivePromotion(PromotionCondition.SPECIFIC_MONTH);
        }

        return promo.map(p -> computeDiscount(p, originalAmount))
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal computeDiscount(Promotion promotion, BigDecimal originalAmount) {
        if (promotion.getDiscountType() == DiscountType.PERCENTAGE) {
            return originalAmount.multiply(promotion.getDiscountValue())
                    .divide(BigDecimal.valueOf(100));
        }
        return promotion.getDiscountValue();
    }

    private boolean isNewMember(MemberDto member) {
        return member.getRegistrationDate().isAfter(LocalDate.now().minusMonths(1));
    }

    private String resolveDiscountReason(PaymentRequestDto dto, MemberDto member) {
        if (dto.getManualDiscountAmount() != null &&
                dto.getManualDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            return dto.getDiscountReason();
        }

        PromotionCondition condition = isNewMember(member)
                ? PromotionCondition.NEW_MEMBER
                : PromotionCondition.ALL_MEMBERS;

        Optional<Promotion> promo = promotionService.findActivePromotion(condition);

        if (promo.isEmpty()) {
            promo = promotionService.findActivePromotion(PromotionCondition.SPECIFIC_MONTH);
        }

        return promo.map(Promotion::getName).orElse(null);
    }

    // Mapper
    private PaymentResponseDto buildResponseDto(Payment payment) {
        return PaymentResponseDto.builder()
                .id(payment.getId())
                .memberId(payment.getMemberId())
                .membershipId(payment.getMembershipId())
                .paymentMethod(payment.getPaymentMethod())
                .originalAmount(payment.getOriginalAmount())
                .discountAmount(payment.getDiscountAmount())
                .finalAmount(payment.getFinalAmount())
                .discountReason(payment.getDiscountReason())
                .voucher(payment.getVoucher())
                .notes(payment.getNotes())
                .status(payment.getStatus())
                .paymentDate(payment.getPaymentDate())
                .build();
    }
}

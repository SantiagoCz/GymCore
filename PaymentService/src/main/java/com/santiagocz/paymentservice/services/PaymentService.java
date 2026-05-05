package com.santiagocz.paymentservice.services;

import com.santiagocz.paymentservice.clients.MemberClient;
import com.santiagocz.paymentservice.clients.SubscriptionClient;
import com.santiagocz.paymentservice.domain.entities.Payment;
import com.santiagocz.paymentservice.domain.entities.Promotion;
import com.santiagocz.paymentservice.domain.enums.DiscountType;
import com.santiagocz.paymentservice.domain.enums.PaymentStatus;
import com.santiagocz.paymentservice.domain.enums.PromotionCondition;
import com.santiagocz.paymentservice.dto.MemberDto;
import com.santiagocz.paymentservice.dto.PaymentRequestDto;
import com.santiagocz.paymentservice.dto.PaymentResponseDto;
import com.santiagocz.paymentservice.dto.SubscriptionDto;
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
    private final SubscriptionClient subscriptionClient;
    private final MemberClient memberClient;

    @Transactional(readOnly = true)
    public List<PaymentResponseDto> findByMemberId(Long memberId) {
        return paymentRepository.findByMemberId(memberId)
                .stream()
                .map(this::buildResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDto> findBySubscriptionId(Long subscriptionId) {
        return paymentRepository.findBySubscriptionId(subscriptionId)
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

    @Transactional
    public PaymentResponseDto create(PaymentRequestDto dto) {

        // 1. Validar que el socio existe y está activo
        MemberDto member = validateMember(dto.getMemberId());

        // 2. Obtener la suscripción y su membresía
        SubscriptionDto subscription = getSubscription(dto.getSubscriptionId());

        // 3. Calcular el descuento
        BigDecimal originalAmount = subscription.getMembership().getPrice();
        BigDecimal discountAmount = calculateDiscount(dto, member, originalAmount);
        String discountReason = resolveDiscountReason(dto, member);

        // 4. Calcular el monto final
        BigDecimal finalAmount = originalAmount.subtract(discountAmount)
                .max(BigDecimal.ZERO); // nunca negativo

        // 5. Registrar el pago
        Payment payment = Payment.builder()
                .subscriptionId(dto.getSubscriptionId())
                .memberId(dto.getMemberId())
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

        PaymentResponseDto response = buildResponseDto(paymentRepository.save(payment));

        // 6. Renovar la suscripción
        subscriptionClient.renew(dto.getSubscriptionId());

        return response;
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

    private SubscriptionDto getSubscription(Long subscriptionId) {
        try {
            return subscriptionClient.getById(subscriptionId);
        } catch (Exception e) {
            throw new RuntimeException("Subscription not found with id: " + subscriptionId);
        }
    }

    private BigDecimal calculateDiscount(PaymentRequestDto dto, MemberDto member, BigDecimal originalAmount) {

        // Si hay descuento manual, tiene prioridad
        if (dto.getManualDiscountAmount() != null &&
                dto.getManualDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            return dto.getManualDiscountAmount();
        }

        // Detectar promoción automática
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
                .subscriptionId(payment.getSubscriptionId())
                .memberId(payment.getMemberId())
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

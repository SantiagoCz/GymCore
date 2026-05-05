package com.santiagocz.paymentservice.dto;

import com.santiagocz.paymentservice.domain.enums.PaymentMethod;
import com.santiagocz.paymentservice.domain.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDto {

    private Long id;
    private Long subscriptionId;
    private Long memberId;
    private PaymentMethod paymentMethod;
    private BigDecimal originalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String discountReason;
    private String voucher;
    private String notes;
    private PaymentStatus status;
    private LocalDateTime paymentDate;
}
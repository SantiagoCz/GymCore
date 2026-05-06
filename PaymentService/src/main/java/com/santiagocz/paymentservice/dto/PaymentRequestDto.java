package com.santiagocz.paymentservice.dto;

import com.santiagocz.paymentservice.domain.enums.PaymentMethod;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDto {

    @NotNull(message = "Member ID is required")
    private Long memberId;

    @NotNull(message = "Membership ID is required")
    private Long membershipId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @DecimalMin(value = "0.0", inclusive = true, message = "Discount amount must be 0 or greater")
    private BigDecimal manualDiscountAmount;

    private String discountReason;

    private String voucher;

    private String notes;
}
package com.santiagocz.paymentservice.dto;

import com.santiagocz.paymentservice.domain.enums.DiscountType;
import com.santiagocz.paymentservice.domain.enums.PromotionCondition;
import com.santiagocz.paymentservice.domain.enums.PromotionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionResponseDto {

    private Long id;
    private String name;
    private String description;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private PromotionCondition condition;
    private LocalDate startDate;
    private LocalDate endDate;
    private PromotionStatus status;
}

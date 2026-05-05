package com.santiagocz.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionDto {
    private Long id;
    private Long memberId;
    private MembershipDto membership;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}

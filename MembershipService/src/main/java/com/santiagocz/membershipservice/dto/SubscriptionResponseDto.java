package com.santiagocz.membershipservice.dto;

import com.santiagocz.membershipservice.domain.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponseDto {

    private Long id;
    private Long memberId;
    private MembershipResponseDto membership;
    private LocalDate startDate;
    private LocalDate endDate;
    private SubscriptionStatus status;
}
package com.santiagocz.membershipservice.dto;

import com.santiagocz.membershipservice.domain.enums.MembershipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipResponseDto {

    private Long id;
    private String name;
    private String description;
    private Integer durationDays;
    private Integer weeklyLimit;
    private BigDecimal price;
    private MembershipStatus status;
}
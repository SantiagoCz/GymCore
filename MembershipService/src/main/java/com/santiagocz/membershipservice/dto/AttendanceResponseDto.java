package com.santiagocz.membershipservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceResponseDto {

    private Long id;
    private Long memberId;
    private String memberFirstName;
    private String memberLastName;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
}
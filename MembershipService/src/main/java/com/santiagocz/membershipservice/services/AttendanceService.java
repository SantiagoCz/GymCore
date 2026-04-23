package com.santiagocz.membershipservice.services;

import com.santiagocz.membershipservice.clients.MemberClient;
import com.santiagocz.membershipservice.domain.entities.Attendance;
import com.santiagocz.membershipservice.dto.AttendanceResponseDto;
import com.santiagocz.membershipservice.dto.MemberDto;
import com.santiagocz.membershipservice.dto.SubscriptionResponseDto;
import com.santiagocz.membershipservice.repositories.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final SubscriptionService subscriptionService;
    private final MemberClient memberClient;

    private static final int DAILY_TRAINING_WINDOW_HOURS = 5; // TODO: fetch from gym config

    @Transactional(readOnly = true)
    public List<AttendanceResponseDto> findByMemberId(Long memberId) {
        validateMemberExists(memberId);
        MemberDto member = memberClient.getMemberById(memberId);
        return attendanceRepository.findByMemberId(memberId)
                .stream()
                .map(a -> buildResponseDto(a, member))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponseDto> findByMemberIdBetweenDates(Long memberId,
                                                                  LocalDateTime from,
                                                                  LocalDateTime to) {
        validateMemberExists(memberId);
        MemberDto member = memberClient.getMemberById(memberId);
        return attendanceRepository.findByMemberIdAndCheckInBetween(memberId, from, to)
                .stream()
                .map(a -> buildResponseDto(a, member))
                .collect(Collectors.toList());
    }

    @Transactional
    public AttendanceResponseDto checkIn(String dni) {

        Long memberId = getIdMemberByDni(dni);

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);

        boolean alreadyAttendedToday = attendanceRepository
                .existsByMemberIdAndCheckInBetween(memberId, startOfDay, endOfDay);

        LocalDateTime checkOut = alreadyAttendedToday
                ? getReEntryCheckOut(memberId, startOfDay, endOfDay)
                : getFirstEntryCheckOut(memberId);

        Attendance attendance = Attendance.builder()
                .memberId(memberId)
                .checkIn(LocalDateTime.now())
                .checkOut(checkOut)
                .build();

        return buildResponseDto(attendanceRepository.save(attendance),
                memberClient.getMemberById(attendance.getMemberId()));
    }

    private LocalDateTime getFirstEntryCheckOut(Long memberId) {
        int weeklyLimit = getWeeklyLimitSubscriptionByMemberId(memberId);

        LocalDateTime startOfWeek = LocalDate.now()
                .with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime endOfWeek = LocalDate.now()
                .with(DayOfWeek.SUNDAY).atTime(23, 59, 59);

        long daysAttendedThisWeek = attendanceRepository
                .countDistinctDaysByMemberIdAndCheckInBetween(memberId, startOfWeek, endOfWeek);

        if (daysAttendedThisWeek >= weeklyLimit) {
            throw new RuntimeException("Weekly attendance limit reached");
        }

        return LocalDateTime.now().plusHours(DAILY_TRAINING_WINDOW_HOURS);
    }

    private Integer getWeeklyLimitSubscriptionByMemberId(Long memberId){
        SubscriptionResponseDto subscription = subscriptionService.findActiveByMemberId(memberId);
        return subscription.getMembership().getWeeklyLimit();
    }

    private LocalDateTime getReEntryCheckOut(Long memberId, LocalDateTime startOfDay, LocalDateTime endOfDay) {
        Attendance firstToday = attendanceRepository
                .findFirstByMemberIdAndCheckInBetweenOrderByCheckInAsc(memberId, startOfDay, endOfDay)
                .orElseThrow(() -> new RuntimeException("Attendance record inconsistency"));

        LocalDateTime windowEnd = firstToday.getCheckIn().plusHours(DAILY_TRAINING_WINDOW_HOURS);

        if (LocalDateTime.now().isAfter(windowEnd)) {
            throw new RuntimeException("Daily training window has expired");
        }

        return windowEnd;
    }

    private void validateMemberExists(Long memberId) {
        try {
            memberClient.getMemberById(memberId);
        } catch (Exception e) {
            throw new RuntimeException("Member not found with id: " + memberId);
        }
    }

    private Long getIdMemberByDni(String dni) {
        try {
            MemberDto member = memberClient.getMemberByDni(dni);
            if (!"ACTIVE".equals(member.getStatus())) {
                throw new RuntimeException("Member is not active");
            }
            return member.getId();
        } catch (Exception e) {
            throw new RuntimeException("Member not found with DNI: " + dni);
        }
    }

    // Mapper
    private AttendanceResponseDto buildResponseDto(Attendance attendance, MemberDto member) {
        return AttendanceResponseDto.builder()
                .id(attendance.getId())
                .memberId(attendance.getMemberId())
                .memberFirstName(member.getFirstName())
                .memberLastName(member.getLastName())
                .checkIn(attendance.getCheckIn())
                .checkOut(attendance.getCheckOut())
                .build();
    }
}

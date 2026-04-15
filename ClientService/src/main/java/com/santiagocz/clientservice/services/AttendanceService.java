package com.santiagocz.clientservice.services;

import com.santiagocz.clientservice.dto.AttendanceResponseDto;
import com.santiagocz.clientservice.domain.entities.Attendance;
import com.santiagocz.clientservice.domain.entities.Member;
import com.santiagocz.clientservice.repositories.AttendanceRepository;
import com.santiagocz.clientservice.repositories.MemberRepository;
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
    private final MemberRepository memberRepository;

    private static final int DAILY_TRAINING_WINDOW_HOURS = 5; // TODO: fetch from gym config
    private static final int WEEKLY_LIMIT = 3; // TODO: fetch from membership-service

    @Transactional(readOnly = true)
    public List<AttendanceResponseDto> findByMemberId(Long memberId) {
        return attendanceRepository.findByMemberId(memberId)
                .stream()
                .map(this::buildResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponseDto> findByMemberIdBetweenDates(
            Long memberId,
            LocalDateTime from,
            LocalDateTime to) {
        return attendanceRepository.findByMemberIdAndCheckInBetween(memberId, from, to)
                .stream()
                .map(this::buildResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AttendanceResponseDto checkIn(String dni) {
        Member member = findMemberByDni(dni);
        Long memberId = member.getId();

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);

        boolean alreadyAttendedToday = attendanceRepository
                .existsByMemberIdAndCheckInBetween(memberId, startOfDay, endOfDay);

        LocalDateTime checkOut = alreadyAttendedToday
                ? getReEntryCheckOut(memberId, startOfDay, endOfDay)
                : getFirstEntryCheckOut(memberId);

        Attendance attendance = Attendance.builder()
                .member(member)
                .checkIn(LocalDateTime.now())
                .checkOut(checkOut)
                .build();

        return buildResponseDto(attendanceRepository.save(attendance));
    }

    private Member findMemberByDni(String dni) {
        return memberRepository.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Member not found with DNI: " + dni));
    }

    private LocalDateTime getFirstEntryCheckOut(Long memberId) {
        LocalDateTime startOfWeek = LocalDate.now()
                .with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime endOfWeek = LocalDate.now()
                .with(DayOfWeek.SUNDAY).atTime(23, 59, 59);

        long daysAttendedThisWeek = attendanceRepository
                .countDistinctDaysByMemberIdAndCheckInBetween(memberId, startOfWeek, endOfWeek);

        if (daysAttendedThisWeek >= WEEKLY_LIMIT) {
            throw new RuntimeException("Weekly attendance limit reached");
        }

        return LocalDateTime.now().plusHours(DAILY_TRAINING_WINDOW_HOURS);
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

    // Mapper
    private AttendanceResponseDto buildResponseDto(Attendance attendance) {
        return AttendanceResponseDto.builder()
                .id(attendance.getId())
                .memberId(attendance.getMember().getId())
                .memberFirstName(attendance.getMember().getFirstName())
                .memberLastName(attendance.getMember().getLastName())
                .checkIn(attendance.getCheckIn())
                .checkOut(attendance.getCheckOut())
                .build();
    }
}
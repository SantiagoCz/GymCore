package com.santiagocz.membershipservice.controllers;

import com.santiagocz.membershipservice.dto.AttendanceResponseDto;
import com.santiagocz.membershipservice.services.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<AttendanceResponseDto>> findByMemberId(
            @PathVariable Long memberId) {
        return ResponseEntity.ok(attendanceService.findByMemberId(memberId));
    }

    @GetMapping("/member/{memberId}/between")
    public ResponseEntity<List<AttendanceResponseDto>> findByMemberIdBetweenDates(
            @PathVariable Long memberId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(attendanceService.findByMemberIdBetweenDates(memberId, from, to));
    }

    @PostMapping("/checkin/{dni}")
    public ResponseEntity<AttendanceResponseDto> checkIn(@PathVariable String dni) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.checkIn(dni));
    }
}

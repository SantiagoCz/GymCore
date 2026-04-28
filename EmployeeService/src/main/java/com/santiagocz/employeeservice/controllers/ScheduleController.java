package com.santiagocz.employeeservice.controllers;

import com.santiagocz.employeeservice.dto.ScheduleRequestDto;
import com.santiagocz.employeeservice.dto.ScheduleResponseDto;
import com.santiagocz.employeeservice.services.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<ScheduleResponseDto>> findByEmployeeId(
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(scheduleService.findByEmployeeId(employeeId));
    }

    @GetMapping("/day/{dayOfWeek}")
    public ResponseEntity<List<ScheduleResponseDto>> findByDayOfWeek(
            @PathVariable DayOfWeek dayOfWeek) {
        return ResponseEntity.ok(scheduleService.findByDayOfWeek(dayOfWeek));
    }

    @PostMapping
    public ResponseEntity<ScheduleResponseDto> create(
            @Valid @RequestBody ScheduleRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScheduleResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ScheduleRequestDto dto) {
        return ResponseEntity.ok(scheduleService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        scheduleService.delete(id);
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("message", "Schedule deleted successfully");
        return ResponseEntity.ok(response);
    }
}
package com.santiagocz.employeeservice.services;

import com.santiagocz.employeeservice.domain.entities.Employee;
import com.santiagocz.employeeservice.domain.entities.Schedule;
import com.santiagocz.employeeservice.domain.enums.EmployeeStatus;
import com.santiagocz.employeeservice.dto.ScheduleRequestDto;
import com.santiagocz.employeeservice.dto.ScheduleResponseDto;
import com.santiagocz.employeeservice.repositories.EmployeeRepository;
import com.santiagocz.employeeservice.repositories.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> findByEmployeeId(Long employeeId) {
        validateEmployeeExists(employeeId);
        return scheduleRepository.findByEmployeeId(employeeId)
                .stream()
                .map(this::buildResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> findByDayOfWeek(DayOfWeek dayOfWeek) {
        return scheduleRepository.findByDayOfWeek(dayOfWeek)
                .stream()
                .map(this::buildResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ScheduleResponseDto create(ScheduleRequestDto dto) {
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + dto.getEmployeeId()));

        if (employee.getStatus() == EmployeeStatus.INACTIVE) {
            throw new RuntimeException("Cannot assign schedule to an inactive employee");
        }

        validateNoOverlap(dto.getEmployeeId(), dto.getDayOfWeek(),
                dto.getStartTime(), dto.getEndTime());

        Schedule schedule = Schedule.builder()
                .employee(employee)
                .dayOfWeek(dto.getDayOfWeek())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .build();

        return buildResponseDto(scheduleRepository.save(schedule));
    }

    @Transactional
    public List<ScheduleResponseDto> createBatch(List<ScheduleRequestDto> dtos) {
        return dtos.stream()
                .map(this::create)
                .collect(Collectors.toList());
    }

    @Transactional
    public ScheduleResponseDto update(Long id, ScheduleRequestDto dto) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found with id: " + id));

        validateNoOverlap(schedule.getEmployee().getId(), dto.getDayOfWeek(),
                dto.getStartTime(), dto.getEndTime());

        schedule.setDayOfWeek(dto.getDayOfWeek());
        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());

        return buildResponseDto(scheduleRepository.save(schedule));
    }

    @Transactional
    public void delete(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found with id: " + id));
        scheduleRepository.delete(schedule);
    }

    private void validateEmployeeExists(Long employeeId) {
        employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
    }

    private void validateNoOverlap(Long employeeId, DayOfWeek day, LocalTime start, LocalTime end) {
        if (scheduleRepository.existsOverlappingSchedule(employeeId, day, start, end)) {
            throw new RuntimeException("Schedule overlaps with existing schedule on " + day);
        }
    }

    // Mapper
    private ScheduleResponseDto buildResponseDto(Schedule schedule) {
        return ScheduleResponseDto.builder()
                .id(schedule.getId())
                .employeeId(schedule.getEmployee().getId())
                .employeeFirstName(schedule.getEmployee().getFirstName())
                .employeeLastName(schedule.getEmployee().getLastName())
                .dayOfWeek(schedule.getDayOfWeek())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .build();
    }
}
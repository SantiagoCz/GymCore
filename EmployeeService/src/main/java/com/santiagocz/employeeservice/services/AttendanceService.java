package com.santiagocz.employeeservice.services;

import com.santiagocz.employeeservice.domain.entities.Attendance;
import com.santiagocz.employeeservice.domain.entities.Employee;
import com.santiagocz.employeeservice.domain.enums.EmployeeStatus;
import com.santiagocz.employeeservice.dto.AttendanceResponseDto;
import com.santiagocz.employeeservice.repositories.AttendanceRepository;
import com.santiagocz.employeeservice.repositories.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public List<AttendanceResponseDto> findByEmployeeId(Long employeeId) {
        validateEmployeeExists(employeeId);
        return attendanceRepository.findByEmployeeId(employeeId)
                .stream()
                .map(this::buildResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponseDto> findByEmployeeIdBetweenDates(
            Long employeeId,
            LocalDateTime from,
            LocalDateTime to) {
        validateEmployeeExists(employeeId);
        return attendanceRepository.findByEmployeeIdAndCheckInBetween(employeeId, from, to)
                .stream()
                .map(this::buildResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AttendanceResponseDto checkIn(String dni) {
        Employee employee = employeeRepository.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Employee not found with DNI: " + dni));

        if (employee.getStatus() == EmployeeStatus.INACTIVE) {
            throw new RuntimeException("Employee is not active");
        }

        if (attendanceRepository.existsByEmployeeIdAndCheckInBetween(
                employee.getId(),
                LocalDate.now().atStartOfDay(),
                LocalDate.now().atTime(23, 59, 59))) {
            throw new RuntimeException("Employee already checked in today");
        }

        Attendance attendance = Attendance.builder()
                .employee(employee)
                .checkIn(LocalDateTime.now())
                .build();

        return buildResponseDto(attendanceRepository.save(attendance));
    }

    @Transactional
    public AttendanceResponseDto checkOut(String dni, String notes) {
        Employee employee = employeeRepository.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Employee not found with DNI: " + dni));

        Attendance attendance = attendanceRepository
                .findFirstByEmployeeIdAndCheckOutIsNull(employee.getId())
                .orElseThrow(() -> new RuntimeException("No active check-in found for employee: " + dni));

        attendance.setCheckOut(LocalDateTime.now());
        attendance.setNotes(notes);

        return buildResponseDto(attendanceRepository.save(attendance));
    }

    private void validateEmployeeExists(Long employeeId) {
        employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
    }

    // Mapper
    private AttendanceResponseDto buildResponseDto(Attendance attendance) {
        return AttendanceResponseDto.builder()
                .id(attendance.getId())
                .employeeId(attendance.getEmployee().getId())
                .employeeFirstName(attendance.getEmployee().getFirstName())
                .employeeLastName(attendance.getEmployee().getLastName())
                .checkIn(attendance.getCheckIn())
                .checkOut(attendance.getCheckOut())
                .notes(attendance.getNotes())
                .build();
    }
}
package com.santiagocz.employeeservice.services;

import com.santiagocz.employeeservice.domain.entities.Employee;
import com.santiagocz.employeeservice.domain.enums.EmployeeRole;
import com.santiagocz.employeeservice.domain.enums.EmployeeStatus;
import com.santiagocz.employeeservice.dto.EmployeeRequestDto;
import com.santiagocz.employeeservice.dto.EmployeeResponseDto;
import com.santiagocz.employeeservice.repositories.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public List<EmployeeResponseDto> findAll() {
        return employeeRepository.findAll()
                .stream()
                .map(this::buildResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmployeeResponseDto findById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        return buildResponseDto(employee);
    }

    @Transactional(readOnly = true)
    public EmployeeResponseDto findByDni(String dni) {
        Employee employee = employeeRepository.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Employee not found with DNI: " + dni));
        return buildResponseDto(employee);
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponseDto> findByStatus(EmployeeStatus status) {
        return employeeRepository.findByStatus(status)
                .stream()
                .map(this::buildResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponseDto> findByRole(EmployeeRole role) {
        return employeeRepository.findByRole(role)
                .stream()
                .map(this::buildResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EmployeeResponseDto create(EmployeeRequestDto dto) {
        if (employeeRepository.existsByDni(dto.getDni())) {
            throw new RuntimeException("Employee already exists with DNI: " + dto.getDni());
        }
        Employee employee = buildEntity(dto);
        employee.setHireDate(LocalDate.now());
        employee.setStatus(EmployeeStatus.ACTIVE);
        return buildResponseDto(employeeRepository.save(employee));
    }

    @Transactional
    public EmployeeResponseDto update(Long id, EmployeeRequestDto dto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        if (!employee.getDni().equals(dto.getDni()) &&
                employeeRepository.existsByDni(dto.getDni())) {
            throw new RuntimeException("Employee already exists with DNI: " + dto.getDni());
        }

        employee.setDni(dto.getDni());
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setBirthDate(dto.getBirthDate());
        employee.setRole(dto.getRole());

        return buildResponseDto(employeeRepository.save(employee));
    }

    @Transactional
    public void delete(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        if (employee.getStatus() == EmployeeStatus.INACTIVE) {
            throw new RuntimeException("Employee is already inactive");
        }

        employee.setStatus(EmployeeStatus.INACTIVE);
        employeeRepository.save(employee);
    }

    // Mappers
    private EmployeeResponseDto buildResponseDto(Employee employee) {
        return EmployeeResponseDto.builder()
                .id(employee.getId())
                .dni(employee.getDni())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .birthDate(employee.getBirthDate())
                .hireDate(employee.getHireDate())
                .role(employee.getRole())
                .status(employee.getStatus())
                .build();
    }

    private Employee buildEntity(EmployeeRequestDto dto) {
        return Employee.builder()
                .dni(dto.getDni())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .birthDate(dto.getBirthDate())
                .role(dto.getRole())
                .build();
    }
}

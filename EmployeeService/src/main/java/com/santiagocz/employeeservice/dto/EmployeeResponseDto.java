package com.santiagocz.employeeservice.dto;

import com.santiagocz.employeeservice.domain.enums.EmployeeRole;
import com.santiagocz.employeeservice.domain.enums.EmployeeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponseDto {

    private Long id;
    private String dni;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private LocalDate hireDate;
    private EmployeeRole role;
    private EmployeeStatus status;
}
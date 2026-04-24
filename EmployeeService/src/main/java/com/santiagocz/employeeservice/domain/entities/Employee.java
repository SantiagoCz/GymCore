package com.santiagocz.employeeservice.domain.entities;

import com.santiagocz.employeeservice.domain.enums.EmployeeRole;
import com.santiagocz.employeeservice.domain.enums.EmployeeStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String dni;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String email;

    private String phone;

    private LocalDate birthDate;

    @Column(nullable = false)
    private LocalDate hireDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeStatus status;
}
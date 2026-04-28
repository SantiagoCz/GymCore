package com.santiagocz.employeeservice.controllers;

import com.santiagocz.employeeservice.domain.enums.EmployeeRole;
import com.santiagocz.employeeservice.domain.enums.EmployeeStatus;
import com.santiagocz.employeeservice.dto.EmployeeRequestDto;
import com.santiagocz.employeeservice.dto.EmployeeResponseDto;
import com.santiagocz.employeeservice.services.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<EmployeeResponseDto>> findAll() {
        return ResponseEntity.ok(employeeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.findById(id));
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<EmployeeResponseDto> findByDni(@PathVariable String dni) {
        return ResponseEntity.ok(employeeService.findByDni(dni));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<EmployeeResponseDto>> findByStatus(@PathVariable EmployeeStatus status) {
        return ResponseEntity.ok(employeeService.findByStatus(status));
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<EmployeeResponseDto>> findByRole(@PathVariable EmployeeRole role) {
        return ResponseEntity.ok(employeeService.findByRole(role));
    }

    @PostMapping
    public ResponseEntity<EmployeeResponseDto> create(@Valid @RequestBody EmployeeRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequestDto dto) {
        return ResponseEntity.ok(employeeService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        employeeService.delete(id);
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("message", "Employee deactivated successfully");
        return ResponseEntity.ok(response);
    }
}

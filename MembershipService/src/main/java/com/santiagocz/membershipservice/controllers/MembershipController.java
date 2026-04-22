package com.santiagocz.membershipservice.controllers;

import com.santiagocz.membershipservice.domain.enums.MembershipStatus;
import com.santiagocz.membershipservice.dto.MembershipRequestDto;
import com.santiagocz.membershipservice.dto.MembershipResponseDto;
import com.santiagocz.membershipservice.services.MembershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/memberships")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @GetMapping
    public ResponseEntity<List<MembershipResponseDto>> findAll() {
        return ResponseEntity.ok(membershipService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MembershipResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(membershipService.findById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<MembershipResponseDto>> findByStatus(
            @PathVariable MembershipStatus status) {
        return ResponseEntity.ok(membershipService.findByStatus(status));
    }

    @PostMapping
    public ResponseEntity<MembershipResponseDto> create(
            @Valid @RequestBody MembershipRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(membershipService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MembershipResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody MembershipRequestDto dto) {
        return ResponseEntity.ok(membershipService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        membershipService.delete(id);
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("message", "Membership deactivated successfully");
        return ResponseEntity.ok(response);
    }
}

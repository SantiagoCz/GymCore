package com.santiagocz.membershipservice.controllers;

import com.santiagocz.membershipservice.domain.enums.SubscriptionStatus;
import com.santiagocz.membershipservice.dto.SubscriptionRequestDto;
import com.santiagocz.membershipservice.dto.SubscriptionResponseDto;
import com.santiagocz.membershipservice.services.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<SubscriptionResponseDto>> findByMemberId(
            @PathVariable Long memberId) {
        return ResponseEntity.ok(subscriptionService.findByMemberId(memberId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<SubscriptionResponseDto>> findByStatus(
            @PathVariable SubscriptionStatus status) {
        return ResponseEntity.ok(subscriptionService.findByStatus(status));
    }

    @GetMapping("/member/{memberId}/active")
    public ResponseEntity<SubscriptionResponseDto> findActiveByMemberId(
            @PathVariable Long memberId) {
        return ResponseEntity.ok(subscriptionService.findActiveByMemberId(memberId));
    }

    @PostMapping
    public ResponseEntity<SubscriptionResponseDto> create(
            @Valid @RequestBody SubscriptionRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(subscriptionService.create(dto));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<SubscriptionResponseDto> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(subscriptionService.cancel(id));
    }
}
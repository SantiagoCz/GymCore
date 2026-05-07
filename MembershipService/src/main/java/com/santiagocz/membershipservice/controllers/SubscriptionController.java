package com.santiagocz.membershipservice.controllers;

import com.santiagocz.membershipservice.domain.enums.SubscriptionStatus;
import com.santiagocz.membershipservice.dto.SubscriptionResponseDto;
import com.santiagocz.membershipservice.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
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
            @RequestParam Long memberId,
            @RequestParam Long membershipId) {
        return ResponseEntity.ok(subscriptionService.create(memberId, membershipId));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<SubscriptionResponseDto> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(subscriptionService.cancel(id));
    }

    @PostMapping("/cancel-active")
    public ResponseEntity<Void> cancelActive(@RequestParam Long memberId) {
        subscriptionService.cancelActive(memberId);
        return ResponseEntity.noContent().build();
    }
}
package com.santiagocz.paymentservice.controllers;

import com.santiagocz.paymentservice.domain.enums.PaymentStatus;
import com.santiagocz.paymentservice.dto.PaymentRequestDto;
import com.santiagocz.paymentservice.dto.PaymentResponseDto;
import com.santiagocz.paymentservice.services.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<PaymentResponseDto>> findByMemberId(
            @PathVariable Long memberId) {
        return ResponseEntity.ok(paymentService.findByMemberId(memberId));
    }

    @GetMapping("/subscription/{membershipId}")
    public ResponseEntity<List<PaymentResponseDto>> findBySubscriptionId(
            @PathVariable Long membershipId) {
        return ResponseEntity.ok(paymentService.findByMembershipId(membershipId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentResponseDto>> findByStatus(
            @PathVariable PaymentStatus status) {
        return ResponseEntity.ok(paymentService.findByStatus(status));
    }

    @GetMapping("/member/{memberId}/between")
    public ResponseEntity<List<PaymentResponseDto>> findByMemberIdAndDateRange(
            @PathVariable Long memberId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(paymentService.findByMemberIdAndDateRange(
                memberId,
                from.atStartOfDay(),
                to.atTime(23, 59, 59)));
    }

    @GetMapping("/between")
    public ResponseEntity<List<PaymentResponseDto>> findByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(paymentService.findByDateRange(
                from.atStartOfDay(),
                to.atTime(23, 59, 59)));
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDto> create(
            @Valid @RequestBody PaymentRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.create(dto));
    }

    @PatchMapping("/{id}/refund")
    public ResponseEntity<PaymentResponseDto> refund(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.refund(id));
    }
}
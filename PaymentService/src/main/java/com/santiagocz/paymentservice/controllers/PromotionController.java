package com.santiagocz.paymentservice.controllers;

import com.santiagocz.paymentservice.domain.enums.PromotionStatus;
import com.santiagocz.paymentservice.dto.PromotionRequestDto;
import com.santiagocz.paymentservice.dto.PromotionResponseDto;
import com.santiagocz.paymentservice.services.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @GetMapping
    public ResponseEntity<List<PromotionResponseDto>> findAll() {
        return ResponseEntity.ok(promotionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromotionResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(promotionService.findById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PromotionResponseDto>> findByStatus(
            @PathVariable PromotionStatus status) {
        return ResponseEntity.ok(promotionService.findByStatus(status));
    }

    @PostMapping
    public ResponseEntity<PromotionResponseDto> create(
            @Valid @RequestBody PromotionRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(promotionService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromotionResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody PromotionRequestDto dto) {
        return ResponseEntity.ok(promotionService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        promotionService.delete(id);
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("message", "Promotion deactivated successfully");
        return ResponseEntity.ok(response);
    }
}
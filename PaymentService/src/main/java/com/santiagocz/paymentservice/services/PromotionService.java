package com.santiagocz.paymentservice.services;

import com.santiagocz.paymentservice.domain.entities.Promotion;
import com.santiagocz.paymentservice.domain.enums.PromotionCondition;
import com.santiagocz.paymentservice.domain.enums.PromotionStatus;
import com.santiagocz.paymentservice.dto.PromotionRequestDto;
import com.santiagocz.paymentservice.dto.PromotionResponseDto;
import com.santiagocz.paymentservice.repositories.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;

    @Transactional(readOnly = true)
    public List<PromotionResponseDto> findAll() {
        return promotionRepository.findAll()
                .stream()
                .map(this::buildResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PromotionResponseDto> findByStatus(PromotionStatus status) {
        return promotionRepository.findByStatus(status)
                .stream()
                .map(this::buildResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PromotionResponseDto findById(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found with id: " + id));
        return buildResponseDto(promotion);
    }

    @Transactional
    public PromotionResponseDto create(PromotionRequestDto dto) {
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new RuntimeException("End date cannot be before start date");
        }
        Promotion promotion = buildEntity(dto);
        promotion.setStatus(PromotionStatus.ACTIVE);
        return buildResponseDto(promotionRepository.save(promotion));
    }

    @Transactional
    public PromotionResponseDto update(Long id, PromotionRequestDto dto) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found with id: " + id));

        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new RuntimeException("End date cannot be before start date");
        }

        promotion.setName(dto.getName());
        promotion.setDescription(dto.getDescription());
        promotion.setDiscountType(dto.getDiscountType());
        promotion.setDiscountValue(dto.getDiscountValue());
        promotion.setCondition(dto.getCondition());
        promotion.setStartDate(dto.getStartDate());
        promotion.setEndDate(dto.getEndDate());

        return buildResponseDto(promotionRepository.save(promotion));
    }

    @Transactional
    public void delete(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found with id: " + id));

        if (promotion.getStatus() == PromotionStatus.INACTIVE) {
            throw new RuntimeException("Promotion is already inactive");
        }

        promotion.setStatus(PromotionStatus.INACTIVE);
        promotionRepository.save(promotion);
    }

    public Optional<Promotion> findActivePromotion(PromotionCondition condition) {
        return promotionRepository.findActivePromotionByCondition(condition, LocalDate.now());
    }

    // Mappers
    private PromotionResponseDto buildResponseDto(Promotion promotion) {
        return PromotionResponseDto.builder()
                .id(promotion.getId())
                .name(promotion.getName())
                .description(promotion.getDescription())
                .discountType(promotion.getDiscountType())
                .discountValue(promotion.getDiscountValue())
                .condition(promotion.getCondition())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .status(promotion.getStatus())
                .build();
    }

    private Promotion buildEntity(PromotionRequestDto dto) {
        return Promotion.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .discountType(dto.getDiscountType())
                .discountValue(dto.getDiscountValue())
                .condition(dto.getCondition())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();
    }
}
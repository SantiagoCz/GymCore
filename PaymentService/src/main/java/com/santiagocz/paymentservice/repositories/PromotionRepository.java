package com.santiagocz.paymentservice.repositories;

import com.santiagocz.paymentservice.domain.entities.Promotion;
import com.santiagocz.paymentservice.domain.enums.PromotionCondition;
import com.santiagocz.paymentservice.domain.enums.PromotionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    List<Promotion> findByStatus(PromotionStatus status);

    @Query("SELECT p FROM Promotion p " +
            "WHERE p.status = 'ACTIVE' " +
            "AND p.startDate <= :today " +
            "AND p.endDate >= :today " +
            "AND p.condition = :condition")
    Optional<Promotion> findActivePromotionByCondition(
            @Param("condition") PromotionCondition condition,
            @Param("today") LocalDate today);
}
package com.santiagocz.membershipservice.repositories;

import com.santiagocz.membershipservice.domain.entities.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByMemberId(Long memberId);

    List<Attendance> findByMemberIdAndCheckInBetween(
            Long memberId,
            LocalDateTime from,
            LocalDateTime to);

    boolean existsByMemberIdAndCheckInBetween(
            Long memberId,
            LocalDateTime from,
            LocalDateTime to);

    Optional<Attendance> findFirstByMemberIdAndCheckInBetweenOrderByCheckInAsc(
            Long memberId,
            LocalDateTime from,
            LocalDateTime to);

    @Query("SELECT COUNT(DISTINCT CAST(a.checkIn AS date)) " +
            "FROM Attendance a " +
            "WHERE a.memberId = :memberId " +
            "AND a.checkIn BETWEEN :from AND :to")
    long countDistinctDaysByMemberIdAndCheckInBetween(
            @Param("memberId") Long memberId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);
}

package com.santiagocz.employeeservice.repositories;

import com.santiagocz.employeeservice.domain.entities.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByEmployeeId(Long employeeId);

    List<Attendance> findByEmployeeIdAndCheckInBetween(
            Long employeeId,
            LocalDateTime from,
            LocalDateTime to);

    boolean existsByEmployeeIdAndCheckInBetween(
            Long employeeId,
            LocalDateTime from,
            LocalDateTime to);

    Optional<Attendance> findFirstByEmployeeIdAndCheckOutIsNull(Long employeeId);
}

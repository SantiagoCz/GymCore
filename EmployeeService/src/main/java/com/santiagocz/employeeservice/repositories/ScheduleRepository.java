package com.santiagocz.employeeservice.repositories;

import com.santiagocz.employeeservice.domain.entities.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByEmployeeId(Long employeeId);

    List<Schedule> findByDayOfWeek(DayOfWeek dayOfWeek);

    @Query("SELECT COUNT(s) > 0 FROM Schedule s " +
            "WHERE s.employee.id = :employeeId " +
            "AND s.dayOfWeek = :dayOfWeek " +
            "AND s.startTime < :endTime " +
            "AND s.endTime > :startTime")
    boolean existsOverlappingSchedule(
            @Param("employeeId") Long employeeId,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);
}
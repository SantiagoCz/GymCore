package com.santiagocz.employeeservice.repositories;

import com.santiagocz.employeeservice.domain.entities.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByEmployeeId(Long employeeId);

    List<Schedule> findByDayOfWeek(DayOfWeek dayOfWeek);

    boolean existsByEmployeeIdAndDayOfWeek(Long employeeId, DayOfWeek dayOfWeek);
}

package com.project.back_end.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.back_end.models.ScheduleException;

@Repository
public interface ScheduleExceptionRepository extends JpaRepository<ScheduleException, Long> {
    
    List<ScheduleException> findByDoctorId(Long doctorId);
    
    List<ScheduleException> findByDoctorIdAndExceptionDate(Long doctorId, LocalDate exceptionDate);
}

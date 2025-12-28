package com.project.back_end.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.project.back_end.models.DoctorSchedule;

@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {
    
    List<DoctorSchedule> findByDoctorId(Long doctorId);
    
    List<DoctorSchedule> findByDoctorIdAndDayOfWeek(Long doctorId, DoctorSchedule.DayOfWeek dayOfWeek);
    
    @Modifying
    @Transactional
    void deleteByDoctorId(Long doctorId);
}

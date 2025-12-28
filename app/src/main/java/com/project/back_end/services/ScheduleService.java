package com.project.back_end.services;

import com.project.back_end.DTO.MessageResponse;
import com.project.back_end.DTO.ScheduleResponse;
import com.project.back_end.models.DoctorSchedule;
import com.project.back_end.models.ScheduleException;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.DoctorScheduleRepository;
import com.project.back_end.repo.ScheduleExceptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final DoctorRepository doctorRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;
    private final ScheduleExceptionRepository scheduleExceptionRepository;

    /**
     * Get doctor schedule
     */
    public ScheduleResponse getDoctorSchedule(Long doctorId) {
        List<DoctorSchedule> schedules = doctorScheduleRepository.findByDoctorId(doctorId);
        return ScheduleResponse.of(schedules);
    }

    /**
     * Set doctor availability schedule
     */
    @Transactional
    public MessageResponse setDoctorSchedule(Long doctorId, List<DoctorSchedule> schedules) {
        try {
            // Delete existing schedules
            doctorScheduleRepository.deleteByDoctorId(doctorId);
            
            // Save new schedules
            for (DoctorSchedule schedule : schedules) {
                schedule.setDoctor(doctorRepository.findById(doctorId).orElse(null));
                doctorScheduleRepository.save(schedule);
            }

            return MessageResponse.success("Schedule updated successfully");
        } catch (Exception e) {
            return MessageResponse.error("Error: " + e.getMessage());
        }
    }

    /**
     * Add schedule exception
     */
    @Transactional
    public MessageResponse addScheduleException(ScheduleException exception) {
        try {
            scheduleExceptionRepository.save(exception);
            return MessageResponse.success("Exception added successfully");
        } catch (Exception e) {
            return MessageResponse.error("Error: " + e.getMessage());
        }
    }
}

package com.project.back_end.services;

import com.project.back_end.DTO.ApiResponse;
import com.project.back_end.DTO.AppointmentListResponse;
import com.project.back_end.DTO.AvailabilityResponse;
import com.project.back_end.DTO.MessageResponse;
import com.project.back_end.models.Appointment;
import com.project.back_end.repo.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    /**
     * Check doctor availability
     */
    public AvailabilityResponse checkDoctorAvailability(Long doctorId, String date, String time) {
        LocalDate appointmentDate = LocalDate.parse(date);
        LocalTime appointmentTime = LocalTime.parse(time);
        
        // Check if there's an existing appointment at this time
        boolean isBooked = appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTime(
            doctorId, appointmentDate, appointmentTime);
        
        return AvailabilityResponse.of(!isBooked);
    }

    /**
     * Book appointment
     */
    @Transactional
    public ApiResponse<Appointment> bookAppointment(Appointment appointment) {
        try {
            // Check availability
            boolean isBooked = appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTime(
                appointment.getDoctor().getId(), 
                appointment.getAppointmentDate(), 
                appointment.getAppointmentTime()
            );

            if (isBooked) {
                return ApiResponse.error("Time slot already booked");
            }

            appointment.setStatus(Appointment.AppointmentStatus.scheduled);
            Appointment saved = appointmentRepository.save(appointment);
            
            return ApiResponse.success("Appointment booked successfully", saved);
        } catch (Exception e) {
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }

    /**
     * Get appointment by ID
     */
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id).orElse(null);
    }

    /**
     * Update appointment
     */
    @Transactional
    public ApiResponse<Appointment> updateAppointment(Appointment appointment) {
        try {
            if (appointmentRepository.findById(appointment.getId()).isEmpty()) {
                return ApiResponse.error("Appointment not found");
            }

            Appointment saved = appointmentRepository.save(appointment);
            return ApiResponse.success("Appointment updated successfully", saved);
        } catch (Exception e) {
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }

    /**
     * Reschedule appointment
     */
    @Transactional
    public ApiResponse<Appointment> rescheduleAppointment(Long id, String date, String time) {
        try {
            Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);
            if (optionalAppointment.isEmpty()) {
                return ApiResponse.error("Appointment not found");
            }

            Appointment appointment = optionalAppointment.get();
            appointment.setAppointmentDate(LocalDate.parse(date));
            appointment.setAppointmentTime(LocalTime.parse(time));
            
            appointmentRepository.save(appointment);
            return ApiResponse.success("Appointment rescheduled successfully", appointment);
        } catch (Exception e) {
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }

    /**
     * Cancel appointment
     */
    @Transactional
    public MessageResponse cancelAppointment(Long id) {
        var appointment = appointmentRepository.findById(id);
        if (appointment.isEmpty()) {
            return MessageResponse.error("Appointment not found");
        }
        
        appointment.get().setStatus(Appointment.AppointmentStatus.cancelled);
        appointment.get().setCancelledAt(LocalDateTime.now());
        appointmentRepository.save(appointment.get());
        return MessageResponse.success("Appointment cancelled successfully");
    }

    /**
     * Complete appointment
     */
    @Transactional
    public MessageResponse completeAppointment(Long id, String notes) {
        var appointment = appointmentRepository.findById(id);
        if (appointment.isEmpty()) {
            return MessageResponse.error("Appointment not found");
        }
        
        appointment.get().setStatus(Appointment.AppointmentStatus.completed);
        appointment.get().setCompletedAt(LocalDateTime.now());
        if (notes != null) {
            appointment.get().setConsultationNotes(notes);
        }
        appointmentRepository.save(appointment.get());
        return MessageResponse.success("Appointment completed successfully");
    }

    /**
     * Filter appointments
     */
    public AppointmentListResponse filterAppointments(String status, String dateFrom, String dateTo, Long doctorId, Long patientId) {
        List<Appointment> appointments;
        
        if (doctorId != null && patientId != null) {
            appointments = appointmentRepository.findByDoctorIdAndPatientId(doctorId, patientId);
        } else if (doctorId != null) {
            appointments = appointmentRepository.findByDoctorIdOrderByAppointmentDateDesc(doctorId);
        } else if (patientId != null) {
            appointments = appointmentRepository.findByPatientIdOrderByAppointmentDateDesc(patientId);
        } else {
            appointments = appointmentRepository.findAll();
        }
        
        return AppointmentListResponse.of(appointments);
    }
}

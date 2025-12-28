package com.project.back_end.services;

import com.project.back_end.DTO.ApiResponse;
import com.project.back_end.DTO.AppointmentListResponse;
import com.project.back_end.DTO.DoctorStatisticsResponse;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Get all active doctors
     */
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findByIsActiveTrue();
    }

    /**
     * Filter doctors by name, specialization, or date
     */
    public List<Doctor> filterDoctors(String name, String specialization, String date) {
        if (name != null && specialization != null) {
            return doctorRepository.findByUsernameContainingIgnoreCaseAndSpecialization(name, specialization);
        } else if (name != null) {
            return doctorRepository.findByUsernameContainingIgnoreCase(name);
        } else if (specialization != null) {
            return doctorRepository.findBySpecialization(specialization);
        } else {
            return doctorRepository.findByIsActiveTrue();
        }
    }

    /**
     * Get doctor by ID
     */
    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id).orElse(null);
    }

    /**
     * Create doctor (admin only)
     */
    @Transactional
    public ApiResponse<Doctor> createDoctor(Doctor doctor) {
        try {
            if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
                return ApiResponse.error("Email already registered");
            }

            doctor.setPasswordHash(passwordEncoder.encode(doctor.getPasswordHash()));
            doctor.setIsActive(true);

            Doctor saved = doctorRepository.save(doctor);
            return ApiResponse.success("Doctor created successfully", saved);
        } catch (Exception e) {
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }

    /**
     * Update doctor profile
     */
    @Transactional
    public ApiResponse<Doctor> updateDoctor(Doctor doctor) {
        try {
            if (doctorRepository.findById(doctor.getId()).isEmpty()) {
                return ApiResponse.error("Doctor not found");
            }

            Doctor saved = doctorRepository.save(doctor);
            return ApiResponse.success("Doctor profile updated successfully", saved);
        } catch (Exception e) {
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }

    /**
     * Deactivate doctor
     */
    @Transactional
    public void deactivateDoctor(Long id) {
        doctorRepository.findById(id).ifPresent(doctor -> {
            doctor.setIsActive(false);
            doctorRepository.save(doctor);
        });
    }

    /**
     * Get doctor appointments
     */
    public AppointmentListResponse getDoctorAppointments(Long doctorId) {
        List<Appointment> appointments = appointmentRepository.findByDoctorIdOrderByAppointmentDateDesc(doctorId);
        return AppointmentListResponse.of(appointments);
    }

    /**
     * Get doctor statistics
     */
    public DoctorStatisticsResponse getDoctorStatistics(Long doctorId) {
        long totalAppointments = appointmentRepository.countByDoctorId(doctorId);
        long completedAppointments = appointmentRepository.countByDoctorIdAndStatus(doctorId, Appointment.AppointmentStatus.completed);
        long upcomingAppointments = appointmentRepository.countByDoctorIdAndStatus(doctorId, Appointment.AppointmentStatus.scheduled);
        
        return DoctorStatisticsResponse.of(totalAppointments, completedAppointments, upcomingAppointments);
    }

    /**
     * Get doctor upcoming appointments
     */
    public AppointmentListResponse getDoctorUpcomingAppointments(Long doctorId) {
        LocalDate today = LocalDate.now();
        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentDateGreaterThanEqualOrderByAppointmentDateAsc(
            doctorId, today);
        return AppointmentListResponse.of(appointments);
    }
}

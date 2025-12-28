package com.project.back_end.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.back_end.DTO.AdminDashboardResponse;
import com.project.back_end.DTO.AuditLogsResponse;
import com.project.back_end.DTO.MessageResponse;
import com.project.back_end.DTO.UsersResponse;
import com.project.back_end.models.Admin;
import com.project.back_end.models.AuditLog;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.AuditLogRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final AuditLogRepository auditLogRepository;

    /**
     * Get admin dashboard statistics
     */
    public AdminDashboardResponse getAdminDashboardStatistics() {
        long totalPatients = patientRepository.count();
        long totalDoctors = doctorRepository.count();
        long totalAppointments = appointmentRepository.count();
        long todayAppointments = appointmentRepository.countByAppointmentDate(LocalDate.now());

        return AdminDashboardResponse.of(totalPatients, totalDoctors, totalAppointments, todayAppointments);
    }

    /**
     * Get audit logs
     */
    public AuditLogsResponse getAuditLogs(String userType, String action, String dateFrom, String dateTo) {
        List<AuditLog> logs = auditLogRepository.findAll();
        // Apply filters if needed
        return AuditLogsResponse.of(logs);
    }

    /**
     * Generate reports
     */
    public Map<String, Object> generateReport(String reportType, String dateFrom, String dateTo, Long doctorId,
            String status) {
        // Implement report generation logic based on reportType
        return Map.of("report", "Generated report data");
    }

    /**
     * Get all users
     */
    public UsersResponse getAllUsers(String userType, Boolean isActive) {
        List<Patient> patients = null;
        List<Doctor> doctors = null;
        List<Admin> admins = null;

        if (userType == null || userType.equals("patient")) {
            patients = isActive != null ? patientRepository.findByIsActive(isActive) : patientRepository.findAll();
        }

        if (userType == null || userType.equals("doctor")) {
            doctors = isActive != null
                    ? (isActive ? doctorRepository.findByIsActiveTrue() : doctorRepository.findByIsActiveFalse())
                    : doctorRepository.findAll();
        }

        if (userType == null || userType.equals("admin")) {
            admins = adminRepository.findAll();
        }

        return UsersResponse.builder().admins(admins).patients(patients).doctors(doctors).build();
    }

    /**
     * Deactivate user
     */
    @Transactional
    public MessageResponse deactivateUser(String userType, Long userId) {
        switch (userType.toLowerCase()) {
            case "patient" -> {
                var patient = patientRepository.findById(userId);
                if (patient.isEmpty())
                    return MessageResponse.error("Patient not found");
                patient.get().setIsActive(false);
                patientRepository.save(patient.get());
            }
            case "doctor" -> {
                var doctor = doctorRepository.findById(userId);
                if (doctor.isEmpty())
                    return MessageResponse.error("Doctor not found");
                doctor.get().setIsActive(false);
                doctorRepository.save(doctor.get());
            }
            case "admin" -> {
                var admin = adminRepository.findById(userId);
                if (admin.isEmpty())
                    return MessageResponse.error("Admin not found");
                admin.get().setIsActive(false);
                adminRepository.save(admin.get());
            }
            default -> {
                return MessageResponse.error("Invalid user type");
            }
        }
        return MessageResponse.success("User deactivated successfully");
    }

    /**
     * Activate user
     */
    @Transactional
    public MessageResponse activateUser(String userType, Long userId) {
        switch (userType.toLowerCase()) {
            case "patient" -> {
                var patient = patientRepository.findById(userId);
                if (patient.isEmpty())
                    return MessageResponse.error("Patient not found");
                patient.get().setIsActive(true);
                patientRepository.save(patient.get());
            }
            case "doctor" -> {
                var doctor = doctorRepository.findById(userId);
                if (doctor.isEmpty())
                    return MessageResponse.error("Doctor not found");
                doctor.get().setIsActive(true);
                doctorRepository.save(doctor.get());
            }
            case "admin" -> {
                var admin = adminRepository.findById(userId);
                if (admin.isEmpty())
                    return MessageResponse.error("Admin not found");
                admin.get().setIsActive(true);
                adminRepository.save(admin.get());
            }
            default -> {
                return MessageResponse.error("Invalid user type");
            }
        }
        return MessageResponse.success("User activated successfully");
    }

    /**
     * Update admin role
     */
    @Transactional
    public MessageResponse updateAdminRole(Long adminId, String role) {
        var admin = adminRepository.findById(adminId);
        if (admin.isEmpty()) {
            return MessageResponse.error("Admin not found");
        }

        try {
            admin.get().setRole(Admin.Role.valueOf(role.toUpperCase()));
            adminRepository.save(admin.get());
            return MessageResponse.success("Admin role updated successfully");
        } catch (IllegalArgumentException e) {
            return MessageResponse.error("Invalid role: " + role);
        }
    }
}

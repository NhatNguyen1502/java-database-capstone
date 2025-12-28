package com.project.back_end.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.back_end.DTO.ApiResponse;
import com.project.back_end.DTO.AppointmentListResponse;
import com.project.back_end.DTO.LoginResponse;
import com.project.back_end.DTO.PrescriptionListResponse;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.models.Prescription;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.repo.PrescriptionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final TokenService tokenService;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Register new patient
     */
    @Transactional
    public LoginResponse<Patient> registerPatient(Patient patient) {
        try {
            // Check if email or username already exists
            if (patientRepository.findByEmail(patient.getEmail()) != null) {
                return LoginResponse.error("Email already registered");
            }
            if (patientRepository.findByUsername(patient.getUsername()) != null) {
                return LoginResponse.error("Username already taken");
            }

            // Hash password
            patient.setPasswordHash(passwordEncoder.encode(patient.getPasswordHash()));
            patient.setIsActive(true);

            Patient saved = patientRepository.save(patient);
            String token = tokenService.generateToken(saved.getEmail());

            return LoginResponse.success(token, saved);
        } catch (Exception e) {
            return LoginResponse.error("Error: " + e.getMessage());
        }
    }

    /**
     * Get patient by ID
     */
    public Patient getPatientById(Long id) {
        return patientRepository.findById(id).orElse(null);
    }

    /**
     * Update patient profile
     */
    @Transactional
    public ApiResponse<Patient> updatePatient(Patient patient) {
        try {
            Optional<Patient> existing = patientRepository.findById(patient.getId());
            if (existing.isEmpty()) {
                return ApiResponse.error("Patient not found");
            }

            // Don't allow email/username change if already taken by another user
            Patient existingByEmail = patientRepository.findByEmail(patient.getEmail());
            if (existingByEmail != null && !existingByEmail.getId().equals(patient.getId())) {
                return ApiResponse.error("Email already in use");
            }

            Patient saved = patientRepository.save(patient);
            return ApiResponse.success("Patient profile updated successfully", saved);
        } catch (Exception e) {
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }

    /**
     * Get patient appointments
     */
    public AppointmentListResponse getPatientAppointments(Long patientId) {
        List<Appointment> appointments = appointmentRepository.findByPatientIdOrderByAppointmentDateDesc(patientId);
        return AppointmentListResponse.of(appointments);
    }

    /**
     * Filter patient appointments
     */
    public AppointmentListResponse filterPatientAppointments(Long patientId, String status, String dateFrom, String dateTo) {
        List<Appointment> appointments;
        
        if (status != null && !status.isEmpty()) {
            Appointment.AppointmentStatus statusEnum = Appointment.AppointmentStatus.valueOf(status.toLowerCase());
            appointments = appointmentRepository.findByPatientIdAndStatus(patientId, statusEnum);
        } else if (dateFrom != null && dateTo != null) {
            LocalDate from = LocalDate.parse(dateFrom);
            LocalDate to = LocalDate.parse(dateTo);
            appointments = appointmentRepository.findByPatientIdAndAppointmentDateBetween(patientId, from, to);
        } else {
            appointments = appointmentRepository.findByPatientIdOrderByAppointmentDateDesc(patientId);
        }
        
        return AppointmentListResponse.of(appointments);
    }

    /**
     * Get patient prescriptions
     */
    public PrescriptionListResponse getPatientPrescriptions(Long patientId) {
        List<Prescription> prescriptions = prescriptionRepository.findByPatientId(patientId);
        return PrescriptionListResponse.of(prescriptions);
    }
}

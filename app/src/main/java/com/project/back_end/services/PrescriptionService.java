package com.project.back_end.services;

import com.project.back_end.DTO.ApiResponse;
import com.project.back_end.DTO.PrescriptionListResponse;
import com.project.back_end.models.Prescription;
import com.project.back_end.repo.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    /**
     * Create prescription
     */
    @Transactional
    public ApiResponse<Prescription> createPrescription(Prescription prescription) {
        try {
            // Check if prescription already exists for this appointment
            if (!prescriptionRepository.findByAppointmentId(prescription.getAppointmentId()).isEmpty()) {
                return ApiResponse.error("Prescription already exists for this appointment");
            }

            prescription.setStatus("active");
            Prescription saved = prescriptionRepository.save(prescription);
            
            return ApiResponse.success("Prescription created successfully", saved);
        } catch (Exception e) {
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }

    /**
     * Get prescription by appointment ID
     */
    public Prescription getPrescriptionByAppointmentId(String appointmentId) {
        List<Prescription> prescriptions = prescriptionRepository.findByAppointmentId(Long.valueOf(appointmentId));
        return prescriptions.isEmpty() ? null : prescriptions.get(0);
    }

    /**
     * Get prescription by ID
     */
    public Prescription getPrescriptionById(String id) {
        return prescriptionRepository.findById(id).orElse(null);
    }

    /**
     * Update prescription
     */
    @Transactional
    public ApiResponse<Prescription> updatePrescription(Prescription prescription) {
        try {
            if (prescriptionRepository.findById(prescription.getId()).isEmpty()) {
                return ApiResponse.error("Prescription not found");
            }

            Prescription saved = prescriptionRepository.save(prescription);
            return ApiResponse.success("Prescription updated successfully", saved);
        } catch (Exception e) {
            return ApiResponse.error("Error: " + e.getMessage());
        }
    }

    /**
     * Get doctor prescriptions
     */
    public PrescriptionListResponse getDoctorPrescriptions(Long doctorId) {
        List<Prescription> prescriptions = prescriptionRepository.findByDoctorId(doctorId);
        return PrescriptionListResponse.of(prescriptions);
    }
}

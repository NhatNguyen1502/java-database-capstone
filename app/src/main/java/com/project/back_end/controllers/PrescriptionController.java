package com.project.back_end.controllers;

import com.project.back_end.DTO.ApiResponse;
import com.project.back_end.DTO.MessageResponse;
import com.project.back_end.DTO.PrescriptionListResponse;
import com.project.back_end.DTO.TokenValidationResponse;
import com.project.back_end.models.Prescription;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.AuthenticationService;
import com.project.back_end.services.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.path}prescriptions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PrescriptionController {
    
    private final PrescriptionService prescriptionService;
    private final PatientService patientService;
    private final AuthenticationService authenticationService;

    /**
     * US-D010: Add Prescription
     * Doctor adds prescription for a completed appointment
     */
    @PostMapping
    public ResponseEntity<?> createPrescription(
            @Valid @RequestBody Prescription prescription,
            @RequestHeader("Authorization") String token) {
        
        TokenValidationResponse validation = authenticationService.validateToken(token, "doctor");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        try {
            Long doctorId = validation.getUserId();
            prescription.setDoctorId(doctorId);
            
            ApiResponse<Prescription> result = prescriptionService.createPrescription(prescription);
            HttpStatus status = result.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MessageResponse.error("Error creating prescription: " + e.getMessage()));
        }
    }

    /**
     * US-P009: View Prescriptions
     * Patient views their prescription by appointment ID
     */
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<?> getPrescriptionByAppointment(
            @PathVariable String appointmentId,
            @RequestHeader("Authorization") String token) {
        
        TokenValidationResponse validation = authenticationService.validateToken(token, "patient", "doctor", "admin");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        try {
            Prescription prescription = prescriptionService.getPrescriptionByAppointmentId(appointmentId);
            if (prescription == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(MessageResponse.error("Prescription not found for this appointment"));
            }
            return ResponseEntity.ok(ApiResponse.success("Prescription retrieved successfully", prescription));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MessageResponse.error("Error fetching prescription: " + e.getMessage()));
        }
    }

    /**
     * US-P009: View All Patient Prescriptions
     * Patient views all their prescriptions
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getPatientPrescriptions(
            @PathVariable Long patientId,
            @RequestHeader("Authorization") String token) {
        
        TokenValidationResponse validation = authenticationService.validateToken(token, "patient", "doctor", "admin");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        try {
            PrescriptionListResponse prescriptions = patientService.getPatientPrescriptions(patientId);
            return ResponseEntity.ok(prescriptions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MessageResponse.error("Error fetching prescriptions: " + e.getMessage()));
        }
    }

    /**
     * Get Prescription by ID (MongoDB ObjectId)
     * Retrieve prescription details by its unique ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPrescriptionById(
            @PathVariable String id,
            @RequestHeader("Authorization") String token) {
        
        TokenValidationResponse validation = authenticationService.validateToken(token, "patient", "doctor", "admin");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        try {
            Prescription prescription = prescriptionService.getPrescriptionById(id);
            if (prescription == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(MessageResponse.error("Prescription not found"));
            }
            return ResponseEntity.ok(ApiResponse.success("Prescription retrieved successfully", prescription));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MessageResponse.error("Error fetching prescription: " + e.getMessage()));
        }
    }

    /**
     * Update Prescription
     * Doctor updates an existing prescription
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePrescription(
            @PathVariable String id,
            @Valid @RequestBody Prescription prescription,
            @RequestHeader("Authorization") String token) {
        
        TokenValidationResponse validation = authenticationService.validateToken(token, "doctor");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        try {
            prescription.setId(id);
            ApiResponse<Prescription> result = prescriptionService.updatePrescription(prescription);
            HttpStatus status = result.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MessageResponse.error("Error updating prescription: " + e.getMessage()));
        }
    }

    /**
     * Get Doctor's Prescriptions
     * Doctor views all prescriptions they have created
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getDoctorPrescriptions(
            @PathVariable Long doctorId,
            @RequestHeader("Authorization") String token) {
        
        TokenValidationResponse validation = authenticationService.validateToken(token, "doctor", "admin");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        try {
            PrescriptionListResponse prescriptions = prescriptionService.getDoctorPrescriptions(doctorId);
            return ResponseEntity.ok(prescriptions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MessageResponse.error("Error fetching prescriptions: " + e.getMessage()));
        }
    }
}

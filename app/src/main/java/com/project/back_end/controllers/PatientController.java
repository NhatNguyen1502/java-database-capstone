package com.project.back_end.controllers;

import com.project.back_end.DTO.ApiResponse;
import com.project.back_end.DTO.AppointmentListResponse;
import com.project.back_end.DTO.Login;
import com.project.back_end.DTO.LoginResponse;
import com.project.back_end.DTO.MessageResponse;
import com.project.back_end.DTO.PrescriptionListResponse;
import com.project.back_end.DTO.TokenValidationResponse;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.path}patient")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PatientController {

    private final PatientService patientService;
    private final AuthenticationService authenticationService;

    /**
     * US-P002: Patient Login
     * Authenticate patient with email and password
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse<Patient>> login(@Valid @RequestBody Login login) {
        LoginResponse<Patient> response = authenticationService.validatePatient(login);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(response);
    }

    /**
     * US-P001: Patient Registration
     * Create a new patient account
     */
    @PostMapping("/register")
    public ResponseEntity<?> createPatient(@Valid @RequestBody Patient patient) {
        try {
            LoginResponse<Patient> result = patientService.registerPatient(patient);
            HttpStatus status = result.isSuccess() ? HttpStatus.CREATED : HttpStatus.CONFLICT;
            return ResponseEntity.status(status).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MessageResponse.error("Error creating patient: " + e.getMessage()));
        }
    }

    /**
     * US-P003: Get Patient Profile
     * Retrieve patient details using token
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPatient(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        
        TokenValidationResponse validation = authenticationService.validateToken(token, "patient");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        try {
            Patient patient = patientService.getPatientById(id);
            if (patient == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(MessageResponse.error("Patient not found"));
            }
            return ResponseEntity.ok(ApiResponse.success("Patient retrieved successfully", patient));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(MessageResponse.error("Patient not found"));
        }
    }

    /**
     * US-P003: Update Patient Profile
     * Update patient information
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody Patient patient,
            @RequestHeader("Authorization") String token) {
        
        TokenValidationResponse validation = authenticationService.validateToken(token, "patient");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        try {
            patient.setId(id);
            ApiResponse<Patient> result = patientService.updatePatient(patient);
            HttpStatus status = result.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(MessageResponse.error("Patient not found or update failed"));
        }
    }

    /**
     * US-P006: View Patient's Appointments
     * Get all appointments for a specific patient
     */
    @GetMapping("/{id}/appointments")
    public ResponseEntity<?> getPatientAppointments(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        
        TokenValidationResponse validation = authenticationService.validateToken(token, "patient");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        try {
            AppointmentListResponse appointments = patientService.getPatientAppointments(id);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MessageResponse.error("Error fetching appointments: " + e.getMessage()));
        }
    }

    /**
     * US-P006: Filter Patient Appointments
     * Filter appointments by status or other conditions
     */
    @GetMapping("/{id}/appointments/filter")
    public ResponseEntity<?> filterPatientAppointments(
            @PathVariable Long id,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestHeader("Authorization") String token) {
        
        TokenValidationResponse validation = authenticationService.validateToken(token, "patient");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        try {
            AppointmentListResponse filteredAppointments = patientService.filterPatientAppointments(
                id, status, dateFrom, dateTo);
            return ResponseEntity.ok(filteredAppointments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MessageResponse.error("Error filtering appointments: " + e.getMessage()));
        }
    }

    /**
     * US-P009: View Patient Prescriptions
     * Get all prescriptions for a specific patient
     */
    @GetMapping("/{id}/prescriptions")
    public ResponseEntity<?> getPatientPrescriptions(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        
        TokenValidationResponse validation = authenticationService.validateToken(token, "patient");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        try {
            PrescriptionListResponse prescriptions = patientService.getPatientPrescriptions(id);
            return ResponseEntity.ok(prescriptions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MessageResponse.error("Error fetching prescriptions: " + e.getMessage()));
        }
    }
}

package com.project.back_end.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.back_end.DTO.ApiResponse;
import com.project.back_end.DTO.AppointmentListResponse;
import com.project.back_end.DTO.MessageResponse;
import com.project.back_end.DTO.TokenValidationResponse;
import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.AuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.path}appointments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private final AuthenticationService authenticationService;

    /**
     * US-P005: Book Appointment
     * Patient books an appointment with a doctor
     */
    @PostMapping
    public ResponseEntity<?> bookAppointment(
            @Valid @RequestBody Appointment appointment,
            @RequestHeader("Authorization") String token) {
        
        TokenValidationResponse validation = authenticationService.validateToken(token, "patient");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        try {
            Long patientId = validation.getUserId();
            appointment.getPatient().setId(patientId);
            
            ApiResponse<Appointment> result = appointmentService.bookAppointment(appointment);
            HttpStatus status = result.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MessageResponse.error("Error booking appointment: " + e.getMessage()));
        }
    }

    /**
     * US-P006, US-D005, US-D006: View Appointments
     * Get appointments by ID or filter
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getAppointment(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        
        TokenValidationResponse validation = authenticationService.validateToken(token, "patient", "doctor", "admin");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        try {
            Appointment appointment = appointmentService.getAppointmentById(id);
            if (appointment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(MessageResponse.error("Appointment not found"));
            }
            return ResponseEntity.ok(ApiResponse.success("Appointment retrieved successfully", appointment));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MessageResponse.error("Error fetching appointment: " + e.getMessage()));
        }
    }

    /**
     * US-D006: View Upcoming Appointments
     * Doctor views their upcoming appointments
     */
    @GetMapping("/doctor/{doctorId}/upcoming")
    public ResponseEntity<?> getDoctorUpcomingAppointments(
            @PathVariable Long doctorId,
            @RequestHeader("Authorization") String token) {
        
        TokenValidationResponse validation = authenticationService.validateToken(token, "doctor", "admin");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        try {
            AppointmentListResponse appointments = doctorService.getDoctorUpcomingAppointments(doctorId);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MessageResponse.error("Error fetching upcoming appointments: " + e.getMessage()));
        }
    }

    /**
     * US-P007: Update/Reschedule Appointment
     * Patient updates appointment details
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody Appointment appointment,
            @RequestHeader("Authorization") String token) {
        
        TokenValidationResponse validation = authenticationService.validateToken(token, "patient", "doctor");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        try {
            appointment.setId(id);
            ApiResponse<Appointment> result = appointmentService.updateAppointment(appointment);
            HttpStatus status = result.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MessageResponse.error("Error updating appointment: " + e.getMessage()));
        }
    }

    /**
     * US-D007: Reschedule Appointment
     * Doctor reschedules an appointment
     */
    @PutMapping("/{id}/reschedule")
    public ResponseEntity<?> rescheduleAppointment(
            @PathVariable Long id,
            @RequestParam String appointmentDate,
            @RequestParam String appointmentTime,
            @RequestHeader("Authorization") String token) {
        
        TokenValidationResponse validation = authenticationService.validateToken(token, "doctor", "admin");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        try {
            ApiResponse<Appointment> result = appointmentService.rescheduleAppointment(id, appointmentDate, appointmentTime);
            HttpStatus status = result.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MessageResponse.error("Error rescheduling appointment: " + e.getMessage()));
        }
    }

    /**
     * US-P008: Cancel Appointment
     * Patient cancels an appointment
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelAppointment(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        
        TokenValidationResponse validation = authenticationService.validateToken(token, "patient", "doctor", "admin");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        try {
            MessageResponse response = appointmentService.cancelAppointment(id);
            HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(MessageResponse.error("Error cancelling appointment: " + e.getMessage()));
        }
    }

    /**
     * US-D008: Mark Appointment as Complete
     * Doctor marks an appointment as completed
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeAppointment(
            @PathVariable Long id,
            @RequestParam(required = false) String notes,
            @RequestHeader("Authorization") String token) {
        
        TokenValidationResponse validation = authenticationService.validateToken(token, "doctor", "admin");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        try {
            MessageResponse response = appointmentService.completeAppointment(id, notes);
            HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(MessageResponse.error("Error completing appointment: " + e.getMessage()));
        }
    }

    /**
     * Filter Appointments
     * Get appointments with filters (status, date range, doctor, patient)
     */
    @GetMapping("/filter")
    public ResponseEntity<?> filterAppointments(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) Long patientId,
            @RequestHeader("Authorization") String token) {
        
        TokenValidationResponse validation = authenticationService.validateToken(token, "patient", "doctor", "admin");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        try {
            AppointmentListResponse appointments = appointmentService.filterAppointments(
                status, dateFrom, dateTo, doctorId, patientId);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MessageResponse.error("Error filtering appointments: " + e.getMessage()));
        }
    }
}

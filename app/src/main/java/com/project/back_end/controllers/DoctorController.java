package com.project.back_end.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.project.back_end.DTO.AvailabilityResponse;
import com.project.back_end.DTO.DoctorListResponse;
import com.project.back_end.DTO.DoctorStatisticsResponse;
import com.project.back_end.DTO.Login;
import com.project.back_end.DTO.LoginResponse;
import com.project.back_end.DTO.MessageResponse;
import com.project.back_end.DTO.ScheduleResponse;
import com.project.back_end.DTO.TokenValidationResponse;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.DoctorSchedule;
import com.project.back_end.models.ScheduleException;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.AuthenticationService;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.ScheduleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.path}doctor")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DoctorController {

    private final DoctorService doctorService;
    private final ScheduleService scheduleService;
    private final AppointmentService appointmentService;
    private final AuthenticationService authenticationService;

    /**
     * US-D001: Doctor Login
     * Authenticate doctor with email and password
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse<Doctor>> doctorLogin(@Valid @RequestBody Login login) {
        LoginResponse<Doctor> response = authenticationService.validateDoctor(login);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(response);
    }

    /**
     * US-P004: Get All Doctors (for patient search)
     * Retrieve list of all active doctors
     */
    @GetMapping
    public ResponseEntity<DoctorListResponse> getAllDoctors() {
        try {
            List<Doctor> doctors = doctorService.getAllDoctors();
            return ResponseEntity.ok(DoctorListResponse.of(doctors));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new DoctorListResponse(List.of(), 0));
        }
    }

    /**
     * US-P004: Search Doctors
     * Filter doctors by specialization, name, or availability
     */
    @GetMapping("/search")
    public ResponseEntity<DoctorListResponse> searchDoctors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String date) {

        try {
            List<Doctor> doctors = doctorService.filterDoctors(name, specialization, date);
            return ResponseEntity.ok(DoctorListResponse.of(doctors));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new DoctorListResponse(List.of(), 0));
        }
    }

    /**
     * US-D002: Get Doctor Profile
     * Retrieve doctor details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Doctor>> getDoctor(@PathVariable Long id) {
        try {
            Doctor doctor = doctorService.getDoctorById(id);
            if (doctor == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Doctor not found"));
            }
            return ResponseEntity.ok(ApiResponse.success(doctor));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Doctor not found"));
        }
    }

    /**
     * US-A008: Create Doctor (Admin only)
     * Register a new doctor in the system
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Doctor>> createDoctor(
            @Valid @RequestBody Doctor doctor,
            @RequestHeader("Authorization") String token) {

        TokenValidationResponse validation = authenticationService.validateToken(token, "admin");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(validation.getMessage()));
        }

        try {
            ApiResponse<Doctor> result = doctorService.createDoctor(doctor);
            HttpStatus status = result.isSuccess() ? HttpStatus.CREATED : HttpStatus.CONFLICT;
            return ResponseEntity.status(status).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error creating doctor: " + e.getMessage()));
        }
    }

    /**
     * US-D002: Update Doctor Profile
     * Update doctor information
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Doctor>> updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody Doctor doctor,
            @RequestHeader("Authorization") String token) {

        TokenValidationResponse validation = authenticationService.validateToken(token, "doctor", "admin");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(validation.getMessage()));
        }

        try {
            doctor.setId(id);
            ApiResponse<Doctor> result = doctorService.updateDoctor(doctor);
            HttpStatus status = result.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Doctor not found or update failed"));
        }
    }

    /**
     * US-A008: Delete Doctor (Admin only - soft delete)
     * Deactivate a doctor account
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteDoctor(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        TokenValidationResponse validation = authenticationService.validateToken(token, "admin");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(MessageResponse.of(validation.getMessage()));
        }

        try {
            doctorService.deactivateDoctor(id);
            return ResponseEntity.ok(MessageResponse.of("Doctor deactivated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(MessageResponse.of("Doctor not found"));
        }
    }

    /**
     * US-D003: Get Doctor's Schedule
     * Retrieve doctor's weekly schedule
     */
    @GetMapping("/{id}/schedule")
    public ResponseEntity<ScheduleResponse> getDoctorSchedule(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        TokenValidationResponse validation = authenticationService.validateToken(token, "patient", "doctor", "admin");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ScheduleResponse(List.of()));
        }

        try {
            ScheduleResponse schedule = scheduleService.getDoctorSchedule(id);
            return ResponseEntity.ok(schedule);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ScheduleResponse(List.of()));
        }
    }

    /**
     * US-D003: Set Doctor Availability
     * Create or update doctor's weekly schedule
     */
    @PostMapping("/{id}/schedule")
    public ResponseEntity<?> setDoctorSchedule(
            @PathVariable Long id,
            @RequestBody List<DoctorSchedule> schedules,
            @RequestHeader("Authorization") String token) {

        TokenValidationResponse validation = authenticationService.validateToken(token, "doctor", "admin");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        try {
            MessageResponse result = scheduleService.setDoctorSchedule(id, schedules);
            HttpStatus status = result.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(MessageResponse.error("Error updating schedule: " + e.getMessage()));
        }
    }

    /**
     * US-D004: Block Time Slots
     * Add schedule exceptions for specific dates
     */
    @PostMapping("/{id}/schedule/exception")
    public ResponseEntity<?> addScheduleException(
            @PathVariable Long id,
            @RequestBody ScheduleException exception,
            @RequestHeader("Authorization") String token) {

        TokenValidationResponse validation = authenticationService.validateToken(token, "doctor", "admin");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        try {
            MessageResponse result = scheduleService.addScheduleException(exception);
            HttpStatus status = result.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(MessageResponse.error("Error adding exception: " + e.getMessage()));
        }
    }

    /**
     * US-D005: View Doctor's Schedule/Appointments
     * Get doctor's appointments for a specific date or date range
     */
    @GetMapping("/{id}/appointments")
    public ResponseEntity<AppointmentListResponse> getDoctorAppointments(
            @PathVariable Long id,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestHeader("Authorization") String token) {

        TokenValidationResponse validation = authenticationService.validateToken(token, "doctor", "admin");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AppointmentListResponse(List.of(), 0));
        }

        try {
            AppointmentListResponse appointments = doctorService.getDoctorAppointments(id);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AppointmentListResponse(List.of(), 0));
        }
    }

    /**
     * US-D011: Doctor Dashboard Statistics
     * Get statistics for doctor's dashboard
     */
    @GetMapping("/{id}/statistics")
    public ResponseEntity<DoctorStatisticsResponse> getDoctorStatistics(
            @PathVariable Long id,
            @RequestParam(required = false) String period,
            @RequestHeader("Authorization") String token) {

        TokenValidationResponse validation = authenticationService.validateToken(token, "doctor", "admin");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new DoctorStatisticsResponse(0, 0, 0));
        }

        try {
            DoctorStatisticsResponse statistics = doctorService.getDoctorStatistics(id);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new DoctorStatisticsResponse(0, 0, 0));
        }
    }

    /**
     * Check doctor availability for a specific date and time
     */
    @GetMapping("/{id}/availability")
    public ResponseEntity<AvailabilityResponse> getDoctorAvailability(
            @PathVariable Long id,
            @RequestParam String date,
            @RequestParam(required = false) String time) {

        try {
            AvailabilityResponse availability = appointmentService.checkDoctorAvailability(id, date, time);
            return ResponseEntity.ok(availability);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AvailabilityResponse.of(false));
        }
    }
}

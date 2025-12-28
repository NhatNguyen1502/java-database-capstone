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

import com.project.back_end.DTO.AdminDashboardResponse;
import com.project.back_end.DTO.AuditLogsResponse;
import com.project.back_end.DTO.Login;
import com.project.back_end.DTO.LoginResponse;
import com.project.back_end.DTO.MessageResponse;
import com.project.back_end.DTO.TokenValidationResponse;
import com.project.back_end.DTO.UsersResponse;
import com.project.back_end.models.Admin;
import com.project.back_end.services.AdminService;
import com.project.back_end.services.AuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.path}admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final AuthenticationService authenticationService;

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AdminController.class);

    /**
     * Admin Login
     * Authenticate admin with username/email and password
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse<Admin>> adminLogin(@Valid @RequestBody Login login) {
        logger.info("Admin login attempt for: " + login.getUsername());
        LoginResponse<Admin> response = authenticationService.validateAdmin(login);
        logger.info("Admin login response: " + response);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(response);
    }

    /**
     * US-A004: Admin Dashboard Statistics
     * Get system-wide statistics for admin dashboard
     */
    @GetMapping("/api/dashboard")
    public ResponseEntity<?> getDashboardStatistics(
            @RequestHeader("Authorization") String token) {
        
        TokenValidationResponse validation = authenticationService.validateToken(token, "admin");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        try {
            AdminDashboardResponse statistics = adminService.getAdminDashboardStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MessageResponse.error("Error fetching dashboard statistics: " + e.getMessage()));
        }
    }

    /**
     * US-A003: View User Activity Logs
     * Get audit logs for system activities
     */
    @GetMapping("/api/audit-logs")
    public ResponseEntity<?> getAuditLogs(
            @RequestParam(required = false) String userType,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestHeader("Authorization") String token) {
        
        TokenValidationResponse validation = authenticationService.validateToken(token, "admin");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        try {
            AuditLogsResponse logs = adminService.getAuditLogs(userType, action, dateFrom, dateTo);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MessageResponse.error("Error fetching audit logs: " + e.getMessage()));
        }
    }

    /**
     * US-A005: Generate Reports
     * Generate system reports based on parameters
     */
    @GetMapping("/api/reports/{reportType}")
    public ResponseEntity<?> generateReport(
            @PathVariable String reportType,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) String status,
            @RequestHeader("Authorization") String token) {
        
        TokenValidationResponse validation = authenticationService.validateToken(token, "admin");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        try {
            // TODO: Implement proper report DTOs when report generation is implemented
            return ResponseEntity.ok(MessageResponse.success("Report generation not yet implemented"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MessageResponse.error("Error generating report: " + e.getMessage()));
        }
    }

    /**
     * US-A001: Manage User Accounts - Get All Users
     * List all users (patients, doctors, admins)
     */
    @GetMapping("/api/users")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(required = false) String userType,
            @RequestParam(required = false) Boolean isActive,
            @RequestHeader("Authorization") String token) {
        
        TokenValidationResponse validation = authenticationService.validateToken(token, "admin");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        try {
            UsersResponse users = adminService.getAllUsers(userType, isActive);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MessageResponse.error("Error fetching users: " + e.getMessage()));
        }
    }

    /**
     * US-A001: Deactivate User Account
     * Soft delete a user account
     */
    @PutMapping("/api/users/{userType}/{userId}/deactivate")
    public ResponseEntity<?> deactivateUser(
            @PathVariable String userType,
            @PathVariable Long userId,
            @RequestHeader("Authorization") String token) {
        
        TokenValidationResponse validation = authenticationService.validateToken(token, "admin");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        try {
            MessageResponse response = adminService.deactivateUser(userType, userId);
            HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(MessageResponse.error("Error deactivating user: " + e.getMessage()));
        }
    }

    /**
     * US-A001: Activate User Account
     * Reactivate a deactivated user account
     */
    @PutMapping("/api/users/{userType}/{userId}/activate")
    public ResponseEntity<?> activateUser(
            @PathVariable String userType,
            @PathVariable Long userId,
            @RequestHeader("Authorization") String token) {
        
        TokenValidationResponse validation = authenticationService.validateToken(token, "admin");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        try {
            MessageResponse response = adminService.activateUser(userType, userId);
            HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(MessageResponse.error("Error activating user: " + e.getMessage()));
        }
    }

    /**
     * US-A002: Assign User Roles
     * Update admin role (super_admin or admin)
     */
    @PutMapping("/api/users/admin/{adminId}/role")
    public ResponseEntity<?> updateAdminRole(
            @PathVariable Long adminId,
            @RequestParam String role,
            @RequestHeader("Authorization") String token) {
        
        TokenValidationResponse validation = authenticationService.validateToken(token, "admin");
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        try {
            MessageResponse response = adminService.updateAdminRole(adminId, role);
            HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(MessageResponse.error("Error updating role: " + e.getMessage()));
        }
    }
}


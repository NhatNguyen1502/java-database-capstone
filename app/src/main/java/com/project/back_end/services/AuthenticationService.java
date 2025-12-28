package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.DTO.LoginResponse;
import com.project.back_end.DTO.TokenValidationResponse;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Validate JWT token and check user roles
     */
    public TokenValidationResponse validateToken(String token, String... allowedRoles) {
        try {
            // Remove "Bearer " prefix if present
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            String email = tokenService.extractEmail(token);
            
            // Check each allowed role
            for (String role : allowedRoles) {
                if (tokenService.validateToken(token, role)) {
                    // Get user ID based on role
                    Long userId = getUserIdByEmailAndRole(email, role);
                    return TokenValidationResponse.success(email, role, userId);
                }
            }

            return TokenValidationResponse.error("Unauthorized");
        } catch (Exception e) {
            return TokenValidationResponse.error("Invalid token: " + e.getMessage());
        }
    }

    private Long getUserIdByEmailAndRole(String email, String role) {
        return switch (role.toLowerCase()) {
            case "admin" -> {
                Admin admin = adminRepository.findByEmail(email);
                yield admin != null ? admin.getId() : null;
            }
            case "doctor" -> {
                Doctor doctor = doctorRepository.findByEmail(email);
                yield doctor != null ? doctor.getId() : null;
            }
            case "patient" -> {
                Patient patient = patientRepository.findByEmail(email);
                yield patient != null ? patient.getId() : null;
            }
            default -> null;
        };
    }

    /**
     * Admin login validation
     */
    public LoginResponse<Admin> validateAdmin(Login login) {
        try {
            Admin admin = adminRepository.findByUsernameOrEmail(login.getUsername(), login.getUsername());
            if (admin == null) {
                return LoginResponse.error("Admin not found");
            }

            if (!passwordEncoder.matches(login.getPassword(), admin.getPasswordHash())) {
                return LoginResponse.error("Invalid password");
            }

            if (!admin.getIsActive()) {
                return LoginResponse.error("Account is deactivated");
            }

            String token = tokenService.generateToken(admin.getEmail());
            return LoginResponse.success(token, admin);
        } catch (Exception e) {
            return LoginResponse.error("Error: " + e.getMessage());
        }
    }

    /**
     * Patient login validation
     */
    public LoginResponse<Patient> validatePatient(Login login) {
        try {
            Patient patient = patientRepository.findByEmailOrUsername(login.getUsername(), login.getUsername());
            if (patient == null) {
                return LoginResponse.error("Patient not found");
            }

            if (!passwordEncoder.matches(login.getPassword(), patient.getPasswordHash())) {
                return LoginResponse.error("Invalid password");
            }

            if (!patient.getIsActive()) {
                return LoginResponse.error("Account is deactivated");
            }

            String token = tokenService.generateToken(patient.getEmail());
            return LoginResponse.success(token, patient);
        } catch (Exception e) {
            return LoginResponse.error("Error: " + e.getMessage());
        }
    }

    /**
     * Doctor login validation
     */
    public LoginResponse<Doctor> validateDoctor(Login login) {
        try {
            Doctor doctor = doctorRepository.findByEmailOrUsername(login.getUsername(), login.getUsername());
            if (doctor == null) {
                return LoginResponse.error("Doctor not found");
            }

            if (!passwordEncoder.matches(login.getPassword(), doctor.getPasswordHash())) {
                return LoginResponse.error("Invalid password");
            }

            if (!doctor.getIsActive()) {
                return LoginResponse.error("Account is deactivated");
            }

            String token = tokenService.generateToken(doctor.getEmail());
            return LoginResponse.success(token, doctor);
        } catch (Exception e) {
            return LoginResponse.error("Error: " + e.getMessage());
        }
    }
}

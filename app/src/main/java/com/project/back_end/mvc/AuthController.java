package com.project.back_end.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.back_end.DTO.Login;
import com.project.back_end.DTO.LoginResponse;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.services.AuthenticationService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * MVC Controller for handling authentication-related views and form submissions
 * Uses Thymeleaf for server-side rendering with proper form validation and error handling
 */
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    /**
     * Show doctor login page
     */
    @GetMapping("/doctor/login")
    public String showDoctorLoginForm(Model model) {
        model.addAttribute("login", new Login());
        return "auth/doctorLogin";
    }

    /**
     * Handle doctor login form submission
     * Uses Spring MVC form binding and validation
     */
    @PostMapping("/doctor/login")
    public String doctorLogin(
            @Valid @ModelAttribute("login") Login login,
            BindingResult bindingResult,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        // Check for validation errors (e.g., empty fields)
        if (bindingResult.hasErrors()) {
            return "auth/doctorLogin";
        }

        // Authenticate using existing service
        LoginResponse<Doctor> response = authenticationService.validateDoctor(login);
        
        if (!response.isSuccess()) {
            model.addAttribute("error", response.getMessage());
            return "auth/doctorLogin";
        }

        // Store authentication info in session
        session.setAttribute("token", response.getToken());
        session.setAttribute("userRole", "doctor");
        session.setAttribute("userId", response.getUser().getId());
        session.setAttribute("userName", response.getUser().getUsername());

        redirectAttributes.addFlashAttribute("success", "Login successful!");
        return "redirect:/doctor/dashboard";
    }

    /**
     * Show patient login page
     */
    @GetMapping("/patient/login")
    public String showPatientLoginForm(Model model) {
        model.addAttribute("login", new Login());
        return "auth/patientLogin";
    }

    /**
     * Handle patient login form submission
     */
    @PostMapping("/patient/login")
    public String patientLogin(
            @Valid @ModelAttribute("login") Login login,
            BindingResult bindingResult,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        if (bindingResult.hasErrors()) {
            return "auth/patientLogin";
        }

        LoginResponse<Patient> response = authenticationService.validatePatient(login);
        
        if (!response.isSuccess()) {
            model.addAttribute("error", response.getMessage());
            return "auth/patientLogin";
        }

        session.setAttribute("token", response.getToken());
        session.setAttribute("userRole", "patient");
        session.setAttribute("userId", response.getUser().getId());
        session.setAttribute("userName", response.getUser().getUsername());

        redirectAttributes.addFlashAttribute("success", "Login successful!");
        return "redirect:/patient/dashboard";
    }

    /**
     * Show admin login page
     */
    @GetMapping("/admin/login")
    public String showAdminLoginForm(Model model) {
        model.addAttribute("login", new Login());
        return "auth/adminLogin";
    }

    /**
     * Handle admin login form submission
     */
    @PostMapping("/admin/login")
    public String adminLogin(
            @Valid @ModelAttribute("login") Login login,
            BindingResult bindingResult,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        if (bindingResult.hasErrors()) {
            return "auth/adminLogin";
        }

        LoginResponse<Admin> response = authenticationService.validateAdmin(login);
        
        if (!response.isSuccess()) {
            model.addAttribute("error", response.getMessage());
            return "auth/adminLogin";
        }

        // Store authentication info in session
        session.setAttribute("token", response.getToken());
        session.setAttribute("userRole", "admin");
        session.setAttribute("userId", response.getUser().getId());
        session.setAttribute("userName", response.getUser().getUsername());

        // Debug logging
        System.out.println("✅ [AuthController] Admin login successful");
        System.out.println("   Session ID: " + session.getId());
        System.out.println("   Token stored: " + (response.getToken() != null));
        System.out.println("   Role stored: admin");
        System.out.println("   User: " + response.getUser().getUsername());

        redirectAttributes.addFlashAttribute("success", "Login successful!");
        return "redirect:/admin/dashboard";
    }

    /**
     * Logout - invalidate session and redirect
     */
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "Logged out successfully!");
        return "redirect:/";
    }
}

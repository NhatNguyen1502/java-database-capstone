package com.project.back_end.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.back_end.DTO.LoginResponse;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * MVC Controller for patient registration
 * Uses server-side form validation with Spring MVC
 */
@Controller
@RequiredArgsConstructor
public class PatientRegistrationController {

    private final PatientService patientService;

    /**
     * Show patient registration form
     */
    @GetMapping("/patient/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "auth/patientRegister";
    }

    /**
     * Handle patient registration form submission
     * Validates input and handles errors gracefully
     */
    @PostMapping("/patient/register")
    public String registerPatient(
            @Valid @ModelAttribute("patient") Patient patient,
            BindingResult bindingResult,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            return "auth/patientRegister";
        }

        // Register patient using existing service
        LoginResponse<Patient> response = patientService.registerPatient(patient);
        
        if (!response.isSuccess()) {
            model.addAttribute("error", response.getMessage());
            return "auth/patientRegister";
        }

        // Auto login after successful registration
        session.setAttribute("token", response.getToken());
        session.setAttribute("userRole", "patient");
        session.setAttribute("userId", response.getUser().getId());
        session.setAttribute("userName", response.getUser().getUsername());

        redirectAttributes.addFlashAttribute("success", "Registration successful! Welcome to Smart Clinic.");
        return "redirect:/patient/dashboard";
    }
}

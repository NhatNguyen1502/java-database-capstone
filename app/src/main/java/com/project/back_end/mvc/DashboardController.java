package com.project.back_end.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.project.back_end.services.AdminService;
import com.project.back_end.services.DoctorService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

/**
 * MVC Controller for rendering dashboard pages with server-side data
 * Checks authentication via session and loads appropriate data for each role
 */
@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final AdminService adminService;
    private final DoctorService doctorService;

    /**
     * Admin Dashboard Page
     * Loads admin statistics and renders with Thymeleaf
     */
    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        // Check if user is logged in
        if (session.getAttribute("token") == null) {
            return "redirect:/admin/login";
        }

        // Load dashboard statistics from service
        try {
            var stats = adminService.getAdminDashboardStatistics();
            model.addAttribute("statistics", stats);
            model.addAttribute("userName", session.getAttribute("userName"));
        } catch (Exception e) {
            model.addAttribute("error", "Error loading dashboard data");
        }
        
        return "admin/adminDashboard";
    }

    /**
     * Doctor Dashboard Page
     * Loads doctor-specific statistics
     */
    @GetMapping("/doctor/dashboard")
    public String doctorDashboard(HttpSession session, Model model) {
        if (session.getAttribute("token") == null) {
            return "redirect:/doctor/login";
        }

        try {
            Long doctorId = (Long) session.getAttribute("userId");
            var stats = doctorService.getDoctorStatistics(doctorId);
            
            model.addAttribute("statistics", stats);
            model.addAttribute("userName", session.getAttribute("userName"));
        } catch (Exception e) {
            model.addAttribute("error", "Error loading dashboard data");
        }
        
        return "doctor/doctorDashboard";
    }

    /**
     * Patient Dashboard Page
     */
    @GetMapping("/patient/dashboard")
    public String patientDashboard(HttpSession session, Model model) {
        if (session.getAttribute("token") == null) {
            return "redirect:/patient/login";
        }

        model.addAttribute("userName", session.getAttribute("userName"));
        return "patient/patientDashboard";
    }
}


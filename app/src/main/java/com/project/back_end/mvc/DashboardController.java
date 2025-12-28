package com.project.back_end.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    /**
     * Admin Dashboard Page
     * Returns the admin dashboard HTML view
     */
    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin/adminDashboard";
    }

    /**
     * Doctor Dashboard Page
     * Returns the doctor dashboard HTML view
     */
    @GetMapping("/doctor/dashboard")
    public String doctorDashboard() {
        return "doctor/doctorDashboard";
    }
}


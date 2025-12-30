package com.project.back_end.security;

import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.project.back_end.DTO.TokenValidationResponse;
import com.project.back_end.services.AuthenticationService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * JWT Authentication Filter
 * Supports both JWT token (for REST API) and session-based authentication (for MVC)
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationService authenticationService;

    public JwtAuthenticationFilter(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String token = null;
        String role = null;

        System.out.println("🔍 [JwtFilter] Processing request: " + request.getRequestURI());

        // 1. Try to get token from Authorization header (REST API)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            role = determineRoleFromPath(request.getRequestURI());
            System.out.println("   ✅ Token from Authorization header, role: " + role);
        }
        // 2. If no token in header, try to get from session (MVC)
        else {
            HttpSession session = request.getSession(false);
            if (session != null) {
                token = (String) session.getAttribute("token");
                role = (String) session.getAttribute("userRole");
                System.out.println("   ✅ Token from session, role: " + role);
                System.out.println("   📝 Session ID: " + session.getId());
            } else {
                System.out.println("   ❌ No session found");
            }
        }

        // Validate token if found
        if (token != null && role != null) {
            TokenValidationResponse validation = authenticationService.validateToken(token, role);

            if (validation.isValid()) {
                // Create authentication object and set into SecurityContext
                JwtAuthentication authentication = new JwtAuthentication(validation.getEmail(), role);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("   ✅ Authentication set: " + validation.getEmail() + " with authorities: " + authentication.getAuthorities());
            } else {
                System.out.println("   ❌ Token validation failed: " + validation.getMessage());
            }
        } else {
            System.out.println("   ⚠️  No token or role found (token: " + (token != null) + ", role: " + role + ")");
        }

        // Continue with filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Determine role based on request path
     */
    private String determineRoleFromPath(String path) {
        if (path.startsWith("/admin/")) {
            return "admin";
        } else if (path.startsWith("/doctor/")) {
            return "doctor";
        } else if (path.startsWith("/patient/")) {
            return "patient";
        }
        return null;
    }

    /**
     * Skip filter for public endpoints (login, static resources)
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // Public endpoints do not require filtering
        return path.equals("/") ||
                path.equals("/index.html") ||
                path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/assets/") ||
                path.startsWith("/images/") ||
                path.startsWith("/pages/") ||
                path.startsWith("/static/") ||
                path.startsWith("/actuator/") ||
                path.endsWith("/login");
    }
}

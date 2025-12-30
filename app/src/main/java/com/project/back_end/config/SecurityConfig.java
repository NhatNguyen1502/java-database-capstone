package com.project.back_end.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.project.back_end.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for REST API
                .authorizeHttpRequests(auth -> auth
                        // ✅ Allow MVC login/register pages (GET) - these show forms
                        .requestMatchers("/admin/login", "/doctor/login", "/patient/login").permitAll()
                        .requestMatchers("/patient/register").permitAll()
                        .requestMatchers("/logout").permitAll()
                        
                        // ✅ Protect MVC dashboard pages - require authentication via session
                        .requestMatchers("/admin/dashboard").hasRole("ADMIN")
                        .requestMatchers("/doctor/dashboard").hasRole("DOCTOR")
                        .requestMatchers("/patient/dashboard").hasRole("PATIENT")
                        
                        // ✅ Protect API endpoints - require JWT token authentication
                        .requestMatchers("/admin/api/**").hasRole("ADMIN")
                        .requestMatchers("/doctor/api/**").hasRole("DOCTOR")
                        .requestMatchers("/patient/api/**").hasRole("PATIENT")
                        
                        // Allow static resources
                        .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/images/**",
                                "/assets/**", "/pages/**", "/static/**", "/templates/**")
                        .permitAll()
                        // Allow actuator endpoints
                        .requestMatchers("/actuator/**").permitAll()
                        // All other requests need authentication
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        // ✅ Use IF_REQUIRED to support both session-based (MVC) and stateless (REST API)
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                // ✅ JWT filter for API endpoints only
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

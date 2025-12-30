package com.project.back_end.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for login form with validation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Login {
    
    @NotBlank(message = "Username or email is required")
    private String username;  // Can be email or username
    
    @NotBlank(message = "Password is required")
    private String password;
}

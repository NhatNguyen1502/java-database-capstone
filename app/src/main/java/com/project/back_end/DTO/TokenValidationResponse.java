package com.project.back_end.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidationResponse {
    private boolean valid;
    private String message;
    private String email;
    private String role;
    private Long userId;

    public static TokenValidationResponse success(String email, String role, Long userId) {
        return new TokenValidationResponse(true, null, email, role, userId);
    }

    public static TokenValidationResponse error(String message) {
        return new TokenValidationResponse(false, message, null, null, null);
    }
}

package com.project.back_end.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse<T> {
    private boolean success;
    private String message;
    private String token;
    private T user;

    public static <T> LoginResponse<T> success(String token, T user) {
        return new LoginResponse<>(true, null, token, user);
    }

    public static <T> LoginResponse<T> error(String message) {
        return new LoginResponse<>(false, message, null, null);
    }
}

package com.familyfinance.api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private UserResponse user;

    @Data
    @Builder
    public static class UserResponse {
        private UUID id;
        private String name;
        private String email;
    }
}

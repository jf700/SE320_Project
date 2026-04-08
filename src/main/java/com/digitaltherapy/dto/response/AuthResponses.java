package com.digitaltherapy.dto.response;

import java.util.UUID;

public final class AuthResponses {

    private AuthResponses() {}

    public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        UserInfo user
    ) {
        public static AuthResponse of(String access, String refresh, long expiresIn, UserInfo user) {
            return new AuthResponse(access, refresh, "Bearer", expiresIn, user);
        }
    }

    public record UserInfo(
        UUID id,
        String name,
        String email,
        boolean onboardingComplete
    ) {}
}

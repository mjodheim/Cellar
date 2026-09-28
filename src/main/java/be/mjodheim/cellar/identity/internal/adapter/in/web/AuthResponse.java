package be.mjodheim.cellar.identity.internal.adapter.in.web;

import be.mjodheim.cellar.identity.internal.application.AuthenticationResult;
import be.mjodheim.cellar.identity.internal.domain.Role;

/**
 * Authentication response returned after register, login or refresh.
 */
record AuthResponse(
        String tokenType,
        String accessToken,
        String refreshToken,
        long expiresInSeconds,
        Long userId,
        String email,
        String displayName,
        Role role
) {
    static AuthResponse from(AuthenticationResult result) {
        return new AuthResponse(
                "Bearer",
                result.accessToken(),
                result.refreshToken(),
                result.expiresInSeconds(),
                result.userId(),
                result.email(),
                result.displayName(),
                result.role()
        );
    }
}

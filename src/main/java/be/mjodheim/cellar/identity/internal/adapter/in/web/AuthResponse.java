package be.mjodheim.cellar.identity.internal.adapter.in.web;

import be.mjodheim.cellar.identity.internal.application.AuthenticationResult;
import be.mjodheim.cellar.identity.internal.domain.Role;

/**
 * Authentication response returned after registration, login or token refresh.
 *
 * @param tokenType token type, currently always {@code Bearer}
 * @param accessToken short-lived JWT access token
 * @param refreshToken opaque refresh token used to renew a session
 * @param expiresInSeconds access-token lifetime in seconds
 * @param userId authenticated user identifier
 * @param email authenticated user email
 * @param displayName authenticated user display name
 * @param role authenticated user role
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
    /**
     * Maps the application authentication result to its HTTP representation.
     *
     * @param result authentication result produced by the application service
     * @return response returned to the client
     */
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

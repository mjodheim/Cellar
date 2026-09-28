package be.mjodheim.cellar.identity.internal.application;

import be.mjodheim.cellar.identity.internal.domain.Role;

/**
 * Result returned by successful authentication operations.
 *
 * @param accessToken short-lived JWT access token
 * @param refreshToken opaque refresh token
 * @param expiresInSeconds access-token lifetime in seconds
 * @param userId authenticated user identifier
 * @param email normalized user email
 * @param displayName user-facing name
 * @param role authenticated user role
 */
public record AuthenticationResult(
        String accessToken,
        String refreshToken,
        long expiresInSeconds,
        Long userId,
        String email,
        String displayName,
        Role role
) {}

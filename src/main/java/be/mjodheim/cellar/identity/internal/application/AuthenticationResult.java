package be.mjodheim.cellar.identity.internal.application;

import be.mjodheim.cellar.identity.internal.domain.Role;

/**
 * Result returned by successful authentication operations.
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

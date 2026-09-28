package be.mjodheim.cellar.identity.internal.adapter.in.web;

import be.mjodheim.cellar.identity.internal.domain.Role;
import be.mjodheim.cellar.identity.internal.domain.User;

import java.time.Instant;

/**
 * Safe user representation that never exposes the stored password hash.
 */
record UserProfileResponse(
        Long id,
        String email,
        String displayName,
        Role role,
        boolean enabled,
        Instant createdAt,
        Instant updatedAt
) {
    static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.id(),
                user.email(),
                user.displayName(),
                user.role(),
                user.enabled(),
                user.createdAt(),
                user.updatedAt()
        );
    }
}

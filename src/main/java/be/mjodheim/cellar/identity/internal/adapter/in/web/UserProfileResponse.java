package be.mjodheim.cellar.identity.internal.adapter.in.web;

import be.mjodheim.cellar.identity.internal.domain.Role;
import be.mjodheim.cellar.identity.internal.domain.User;

import java.time.Instant;

/**
 * Safe HTTP representation of a user account that never exposes the stored password hash.
 *
 * @param id user identifier
 * @param email normalized email
 * @param displayName user-facing name
 * @param role functional role
 * @param enabled whether authentication is currently allowed
 * @param createdAt creation timestamp
 * @param updatedAt last modification timestamp
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
    /**
     * Maps a domain user to the public profile response.
     *
     * @param user domain user
     * @return safe profile representation
     */
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

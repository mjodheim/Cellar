package be.mjodheim.cellar.identity.internal.domain;

import java.time.Instant;
import java.util.Objects;

/**
 * Persistent representation of an opaque refresh token.
 *
 * <p>Only the SHA-256 hash of the token is persisted. The raw token is returned once
 * to the client and is never stored by Cellar.</p>
 */
public final class RefreshToken {

    private final Long id;
    private final Long userId;
    private final String tokenHash;
    private final Instant expiresAt;
    private final Instant createdAt;
    private Instant revokedAt;

    private RefreshToken(
            Long id,
            Long userId,
            String tokenHash,
            Instant expiresAt,
            Instant createdAt,
            Instant revokedAt
    ) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User id must be positive");
        }
        if (tokenHash == null || tokenHash.length() != 64) {
            throw new IllegalArgumentException("Refresh token hash must be a SHA-256 hexadecimal hash");
        }
        this.id = id;
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.expiresAt = Objects.requireNonNull(expiresAt);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.revokedAt = revokedAt;
    }

    /** Issues a new refresh token record from a generated token hash. */
    public static RefreshToken issue(Long userId, String tokenHash, Instant expiresAt, Instant now) {
        if (!expiresAt.isAfter(now)) {
            throw new IllegalArgumentException("Refresh token expiration must be in the future");
        }
        return new RefreshToken(null, userId, tokenHash, expiresAt, now, null);
    }

    /** Rebuilds a persisted refresh token. */
    public static RefreshToken rehydrate(
            Long id,
            Long userId,
            String tokenHash,
            Instant expiresAt,
            Instant createdAt,
            Instant revokedAt
    ) {
        return new RefreshToken(id, userId, tokenHash, expiresAt, createdAt, revokedAt);
    }

    /** Revokes the token. Revocation is idempotent. */
    public void revoke(Instant now) {
        if (revokedAt == null) {
            revokedAt = Objects.requireNonNull(now);
        }
    }

    /** Returns whether the token can still be used at the given instant. */
    public boolean isActiveAt(Instant now) {
        return revokedAt == null && expiresAt.isAfter(now);
    }

    public Long id() { return id; }
    public Long userId() { return userId; }
    public String tokenHash() { return tokenHash; }
    public Instant expiresAt() { return expiresAt; }
    public Instant createdAt() { return createdAt; }
    public Instant revokedAt() { return revokedAt; }
}

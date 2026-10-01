package be.mjodheim.cellar.identity.internal.domain;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Domain representation of a Cellar user account.
 *
 * <p>The domain never stores or receives a clear-text password. Only a password hash
 * produced by the security adapter is accepted. Deletion is logical so historical
 * business data can remain attributable to the former account.</p>
 */
public final class User {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final Long id;
    private final String email;
    private final Instant createdAt;
    private String displayName;
    private String passwordHash;
    private Role role;
    private boolean enabled;
    private Instant updatedAt;
    private Instant deletedAt;

    private User(Long id, String email, String displayName, String passwordHash, Role role,
                 boolean enabled, Instant createdAt, Instant updatedAt, Instant deletedAt) {
        this.id = id;
        this.email = normalizeEmail(email);
        this.displayName = requireDisplayName(displayName);
        this.passwordHash = requirePasswordHash(passwordHash);
        this.role = Objects.requireNonNull(role, "Role is required");
        this.enabled = enabled;
        this.createdAt = Objects.requireNonNull(createdAt, "Creation date is required");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Update date is required");
        this.deletedAt = deletedAt;
    }

    /**
     * Creates a new active account from an already hashed password.
     *
     * @param email account email
     * @param displayName user-facing name
     * @param passwordHash already encoded password
     * @param role initial role
     * @param now creation timestamp
     * @return new user aggregate
     */
    public static User register(String email, String displayName, String passwordHash, Role role, Instant now) {
        Objects.requireNonNull(now, "Current date is required");
        return new User(null, email, displayName, passwordHash, role, true, now, now, null);
    }

    /**
     * Rebuilds an account previously stored by a persistence adapter.
     *
     * @param id persisted identifier
     * @param email account email
     * @param displayName user-facing name
     * @param passwordHash stored password hash
     * @param role current role
     * @param enabled authentication state
     * @param createdAt creation timestamp
     * @param updatedAt last modification timestamp
     * @param deletedAt soft-delete timestamp, or {@code null}
     * @return rehydrated user aggregate
     */
    public static User rehydrate(Long id, String email, String displayName, String passwordHash, Role role,
                                 boolean enabled, Instant createdAt, Instant updatedAt, Instant deletedAt) {
        return new User(id, email, displayName, passwordHash, role, enabled, createdAt, updatedAt, deletedAt);
    }

    /** Changes the human-readable profile name. @param displayName new display name @param now modification timestamp */
    public void changeDisplayName(String displayName, Instant now) {
        ensureNotDeleted();
        this.displayName = requireDisplayName(displayName);
        this.updatedAt = Objects.requireNonNull(now);
    }

    /** Replaces the stored password hash. @param passwordHash new hash @param now modification timestamp */
    public void changePasswordHash(String passwordHash, Instant now) {
        ensureNotDeleted();
        this.passwordHash = requirePasswordHash(passwordHash);
        this.updatedAt = Objects.requireNonNull(now);
    }

    /** Changes the functional role. @param role new role @param now modification timestamp */
    public void changeRole(Role role, Instant now) {
        ensureNotDeleted();
        this.role = Objects.requireNonNull(role);
        this.updatedAt = Objects.requireNonNull(now);
    }

    /** Disables authentication without deleting the account. @param now modification timestamp */
    public void disable(Instant now) {
        ensureNotDeleted();
        if (!enabled) return;
        enabled = false;
        updatedAt = Objects.requireNonNull(now);
    }

    /** Re-enables a non-deleted account. @param now modification timestamp */
    public void enable(Instant now) {
        ensureNotDeleted();
        if (enabled) return;
        enabled = true;
        updatedAt = Objects.requireNonNull(now);
    }

    /** Soft-deletes the account. @param now deletion timestamp */
    public void softDelete(Instant now) {
        if (deletedAt != null) return;
        deletedAt = Objects.requireNonNull(now);
        enabled = false;
        updatedAt = now;
    }

    /** @return {@code true} when the account is soft-deleted */
    public boolean isDeleted() { return deletedAt != null; }

    private void ensureNotDeleted() {
        if (isDeleted()) throw new IllegalStateException("Deleted user cannot be modified");
    }

    private static String normalizeEmail(String value) {
        if (value == null) throw new IllegalArgumentException("Email is required");
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        if (!EMAIL_PATTERN.matcher(normalized).matches()) throw new IllegalArgumentException("Email format is invalid");
        return normalized;
    }

    private static String requireDisplayName(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Display name is required");
        return value.trim();
    }

    private static String requirePasswordHash(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Password hash is required");
        return value;
    }

    /** @return persisted identifier, or {@code null} before persistence */
    public Long id() { return id; }
    /** @return normalized immutable email */
    public String email() { return email; }
    /** @return display name */
    public String displayName() { return displayName; }
    /** @return stored password hash */
    public String passwordHash() { return passwordHash; }
    /** @return current role */
    public Role role() { return role; }
    /** @return whether authentication is enabled */
    public boolean enabled() { return enabled; }
    /** @return creation timestamp */
    public Instant createdAt() { return createdAt; }
    /** @return last modification timestamp */
    public Instant updatedAt() { return updatedAt; }
    /** @return soft-delete timestamp, or {@code null} */
    public Instant deletedAt() { return deletedAt; }
}

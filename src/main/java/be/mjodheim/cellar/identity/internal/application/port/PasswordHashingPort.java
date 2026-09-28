package be.mjodheim.cellar.identity.internal.application.port;

/**
 * Cryptographic password hashing boundary.
 */
public interface PasswordHashingPort {

    /**
     * Hashes a clear-text password.
     *
     * @param rawPassword clear-text password
     * @return encoded password hash
     */
    String hash(String rawPassword);

    /**
     * Verifies a clear-text password against a stored hash.
     *
     * @param rawPassword submitted password
     * @param passwordHash stored hash
     * @return {@code true} when the password matches
     */
    boolean matches(String rawPassword, String passwordHash);
}

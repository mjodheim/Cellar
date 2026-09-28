package be.mjodheim.cellar.identity.internal.application.port;

/**
 * Cryptographic password hashing boundary.
 */
public interface PasswordHashingPort {
    String hash(String rawPassword);
    boolean matches(String rawPassword, String passwordHash);
}

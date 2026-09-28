package be.mjodheim.cellar.identity.internal.application;

/**
 * Generic authentication failure that does not reveal which credential was wrong.
 */
public class InvalidCredentialsException extends RuntimeException {

    /**
     * Creates a deliberately generic authentication error.
     */
    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}

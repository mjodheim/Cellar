package be.mjodheim.cellar.identity.internal.application;

/**
 * Raised when an active account already uses the requested email address.
 */
public class EmailAlreadyRegisteredException extends RuntimeException {

    /**
     * Creates the exception for the conflicting email.
     *
     * @param email duplicated email address
     */
    public EmailAlreadyRegisteredException(String email) {
        super("Email '" + email + "' is already registered");
    }
}

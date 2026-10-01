package be.mjodheim.cellar.identity.internal.application;

/**
 * Raised when an expected user account cannot be found.
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Creates the exception for a missing email.
     *
     * @param email missing user email
     */
    public UserNotFoundException(String email) {
        super("User '" + email + "' was not found");
    }

    /**
     * Creates the exception for a missing identifier.
     *
     * @param id missing user identifier
     */
    public UserNotFoundException(Long id) {
        super("User with id " + id + " was not found");
    }
}

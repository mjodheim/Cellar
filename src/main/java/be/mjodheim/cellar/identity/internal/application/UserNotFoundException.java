package be.mjodheim.cellar.identity.internal.application;

/** Raised when an expected user account cannot be found. */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String email) {
        super("User '" + email + "' was not found");
    }

    public UserNotFoundException(Long id) {
        super("User with id " + id + " was not found");
    }
}

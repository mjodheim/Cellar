package be.mjodheim.cellar.identity.internal.application;

/** Raised when a refresh token is unknown, revoked or expired. */
public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException() {
        super("Refresh token is invalid or expired");
    }
}

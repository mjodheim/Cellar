package be.mjodheim.cellar.identity.internal.application.port;

import be.mjodheim.cellar.identity.internal.domain.User;

/**
 * Boundary used by the application layer to issue short-lived access tokens.
 */
public interface AccessTokenPort {

    /**
     * Generates an access token for a user.
     *
     * @param user authenticated user
     * @return encoded access token
     */
    String generate(User user);

    /**
     * Returns the configured access-token lifetime.
     *
     * @return token lifetime in seconds
     */
    long expiresInSeconds();
}

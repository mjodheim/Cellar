package be.mjodheim.cellar.identity.internal.application.port;

import be.mjodheim.cellar.identity.internal.domain.User;

/**
 * Issues short-lived access tokens.
 */
public interface AccessTokenPort {
    String generate(User user);
    long expiresInSeconds();
}

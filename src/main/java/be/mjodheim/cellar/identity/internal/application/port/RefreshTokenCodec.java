package be.mjodheim.cellar.identity.internal.application.port;

/**
 * Generates opaque refresh tokens and hashes raw token material for storage.
 */
public interface RefreshTokenCodec {

    GeneratedRefreshToken generate();

    String hash(String rawToken);

    record GeneratedRefreshToken(String rawToken, String tokenHash) {}
}

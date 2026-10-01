package be.mjodheim.cellar.identity.internal.application.port;

/**
 * Generates opaque refresh tokens and hashes raw token material for storage.
 */
public interface RefreshTokenCodec {

    /**
     * Generates a new raw refresh token together with its persistence-safe hash.
     *
     * @return generated raw token and hash
     */
    GeneratedRefreshToken generate();

    /**
     * Hashes a raw refresh token deterministically.
     *
     * @param rawToken raw token received from a client
     * @return hexadecimal token hash
     */
    String hash(String rawToken);

    /**
     * Pair produced when a new refresh token is generated.
     *
     * @param rawToken value returned to the client
     * @param tokenHash value safe to persist
     */
    record GeneratedRefreshToken(String rawToken, String tokenHash) {}
}

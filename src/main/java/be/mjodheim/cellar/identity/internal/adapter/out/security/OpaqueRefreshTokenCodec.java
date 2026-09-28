package be.mjodheim.cellar.identity.internal.adapter.out.security;

import be.mjodheim.cellar.identity.internal.application.port.RefreshTokenCodec;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Generates cryptographically random opaque refresh tokens.
 *
 * <p>The client receives the raw Base64URL value. Only its SHA-256 hash is persisted.</p>
 */
@Component
class OpaqueRefreshTokenCodec implements RefreshTokenCodec {

    private static final int TOKEN_BYTES = 32;
    private final SecureRandom secureRandom = new SecureRandom();

    /** {@inheritDoc} */
    @Override
    public GeneratedRefreshToken generate() {
        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        return new GeneratedRefreshToken(raw, hash(raw));
    }

    /** {@inheritDoc} */
    @Override
    public String hash(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException("Refresh token is required");
        }

        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}

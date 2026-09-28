package be.mjodheim.cellar.identity.internal.application.port;

import be.mjodheim.cellar.identity.internal.domain.RefreshToken;

import java.util.List;
import java.util.Optional;

/**
 * Persistence port for refresh-token state.
 */
public interface RefreshTokenRepository {
    RefreshToken save(RefreshToken token);
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    List<RefreshToken> findActiveByUserId(Long userId);
}

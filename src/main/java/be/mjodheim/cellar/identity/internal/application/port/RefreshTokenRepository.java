package be.mjodheim.cellar.identity.internal.application.port;

import be.mjodheim.cellar.identity.internal.domain.RefreshToken;

import java.util.List;
import java.util.Optional;

/**
 * Persistence port for refresh-token state.
 */
public interface RefreshTokenRepository {

    /**
     * Persists a refresh-token record.
     *
     * @param token token state to save
     * @return persisted token
     */
    RefreshToken save(RefreshToken token);

    /**
     * Finds a token by its stored hash.
     *
     * @param tokenHash SHA-256 hexadecimal hash
     * @return matching token or an empty optional
     */
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    /**
     * Lists currently active refresh tokens for one user.
     *
     * @param userId user identifier
     * @return active refresh tokens
     */
    List<RefreshToken> findActiveByUserId(Long userId);

    /** Reads the owner without caching a token entity before locking it. */
    Optional<Long> findUserIdByTokenHash(String tokenHash);

    /** Locks a token for rotation or logout until the transaction completes. */
    Optional<RefreshToken> findByTokenHashForUpdate(String tokenHash);
}

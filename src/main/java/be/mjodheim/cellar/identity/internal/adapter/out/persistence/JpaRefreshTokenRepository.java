package be.mjodheim.cellar.identity.internal.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data repository for persisted refresh-token records.
 */
interface JpaRefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    /**
     * Finds a refresh token by its stored SHA-256 hash.
     *
     * @param tokenHash hexadecimal token hash
     * @return matching entity or an empty optional
     */
    Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);

    /**
     * Lists non-revoked tokens that have not yet expired for a user.
     *
     * @param userId user identifier
     * @param now instant used for the expiration comparison
     * @return active refresh-token entities
     */
    List<RefreshTokenEntity> findByUserIdAndRevokedAtIsNullAndExpiresAtAfter(Long userId, Instant now);
}

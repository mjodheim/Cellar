package be.mjodheim.cellar.identity.internal.adapter.out.persistence;

import be.mjodheim.cellar.identity.internal.application.port.RefreshTokenRepository;
import be.mjodheim.cellar.identity.internal.domain.RefreshToken;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Persistence adapter implementing the refresh-token repository port.
 */
@Repository
class RefreshTokenPersistenceAdapter implements RefreshTokenRepository {

    private final JpaRefreshTokenRepository repository;

    RefreshTokenPersistenceAdapter(JpaRefreshTokenRepository repository) {
        this.repository = repository;
    }

    /** {@inheritDoc} */
    @Override
    public RefreshToken save(RefreshToken token) {
        return IdentityPersistenceMapper.toDomain(repository.save(IdentityPersistenceMapper.toEntity(token)));
    }

    /** {@inheritDoc} */
    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return repository.findByTokenHash(tokenHash).map(IdentityPersistenceMapper::toDomain);
    }

    /** {@inheritDoc} */
    @Override
    public List<RefreshToken> findActiveByUserId(Long userId) {
        return repository.findByUserIdAndRevokedAtIsNullAndExpiresAtAfter(userId, Instant.now()).stream()
                .map(IdentityPersistenceMapper::toDomain)
                .toList();
    }
}

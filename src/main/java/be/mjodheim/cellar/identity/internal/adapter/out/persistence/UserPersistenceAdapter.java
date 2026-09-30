package be.mjodheim.cellar.identity.internal.adapter.out.persistence;

import be.mjodheim.cellar.identity.internal.application.port.UserRepository;
import be.mjodheim.cellar.identity.internal.domain.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Persistence adapter implementing the Identity user repository port.
 */
@Repository
class UserPersistenceAdapter implements UserRepository {

    private final JpaUserRepository repository;

    UserPersistenceAdapter(JpaUserRepository repository) {
        this.repository = repository;
    }

    /** {@inheritDoc} */
    @Override
    public User save(User user) {
        return IdentityPersistenceMapper.toDomain(repository.save(IdentityPersistenceMapper.toEntity(user)));
    }

    /** {@inheritDoc} */
    @Override
    public Optional<User> findById(Long id) {
        return repository.findById(id)
                .filter(entity -> entity.deletedAt() == null)
                .map(IdentityPersistenceMapper::toDomain);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmailIgnoreCaseAndDeletedAtIsNull(email)
                .map(IdentityPersistenceMapper::toDomain);
    }

    /** {@inheritDoc} */
    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmailIgnoreCaseAndDeletedAtIsNull(email);
    }

    @Override
    public Optional<User> findByIdForUpdate(Long id) {
        return repository.findByIdForUpdate(id).map(IdentityPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmailForUpdate(String email) {
        return repository.findByEmailForUpdate(email).map(IdentityPersistenceMapper::toDomain);
    }
}

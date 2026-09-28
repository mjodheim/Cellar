package be.mjodheim.cellar.identity.internal.adapter.out.persistence;

import be.mjodheim.cellar.identity.internal.application.port.UserRepository;
import be.mjodheim.cellar.identity.internal.domain.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class UserPersistenceAdapter implements UserRepository {

    private final JpaUserRepository repository;

    UserPersistenceAdapter(JpaUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User save(User user) {
        return IdentityPersistenceMapper.toDomain(repository.save(IdentityPersistenceMapper.toEntity(user)));
    }

    @Override
    public Optional<User> findById(Long id) {
        return repository.findById(id)
                .filter(entity -> entity.deletedAt() == null)
                .map(IdentityPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmailIgnoreCaseAndDeletedAtIsNull(email)
                .map(IdentityPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmailIgnoreCaseAndDeletedAtIsNull(email);
    }
}

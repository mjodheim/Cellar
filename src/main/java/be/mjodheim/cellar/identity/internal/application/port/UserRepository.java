package be.mjodheim.cellar.identity.internal.application.port;

import be.mjodheim.cellar.identity.internal.domain.User;

import java.util.Optional;

/**
 * Persistence port for user accounts.
 */
public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}

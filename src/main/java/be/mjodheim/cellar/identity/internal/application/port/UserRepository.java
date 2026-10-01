package be.mjodheim.cellar.identity.internal.application.port;

import be.mjodheim.cellar.identity.internal.domain.User;

import java.util.Optional;

/**
 * Persistence port for user accounts.
 */
public interface UserRepository {

    /**
     * Persists a user account.
     *
     * @param user account to save
     * @return persisted account
     */
    User save(User user);

    /**
     * Finds a non-deleted user by identifier.
     *
     * @param id user identifier
     * @return matching user or an empty optional
     */
    Optional<User> findById(Long id);

    /**
     * Finds a non-deleted user by email.
     *
     * @param email normalized or case-insensitive email
     * @return matching user or an empty optional
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether an active email already exists.
     *
     * @param email email to check
     * @return {@code true} when a non-deleted user uses the email
     */
    boolean existsByEmail(String email);

    /** Locks an account until the current transaction completes. */
    Optional<User> findByIdForUpdate(Long id);

    /** Locks a non-deleted account by normalized email for login. */
    Optional<User> findByEmailForUpdate(String email);
}

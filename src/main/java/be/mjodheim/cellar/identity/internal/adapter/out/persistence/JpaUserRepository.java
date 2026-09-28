package be.mjodheim.cellar.identity.internal.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data repository for the Identity user persistence model.
 */
interface JpaUserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Finds a non-deleted user by email, ignoring letter case.
     *
     * @param email account email
     * @return matching entity or an empty optional
     */
    Optional<UserEntity> findByEmailIgnoreCaseAndDeletedAtIsNull(String email);

    /**
     * Checks whether a non-deleted user already uses an email.
     *
     * @param email account email
     * @return {@code true} when a matching entity exists
     */
    boolean existsByEmailIgnoreCaseAndDeletedAtIsNull(String email);
}

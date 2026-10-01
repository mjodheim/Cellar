package be.mjodheim.cellar.identity.internal.adapter.out.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from UserEntity u where u.id = :id and u.deletedAt is null")
    Optional<UserEntity> findByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from UserEntity u where lower(u.email) = lower(:email) and u.deletedAt is null")
    Optional<UserEntity> findByEmailForUpdate(@Param("email") String email);
}

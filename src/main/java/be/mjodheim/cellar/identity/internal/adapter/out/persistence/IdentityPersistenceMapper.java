package be.mjodheim.cellar.identity.internal.adapter.out.persistence;

import be.mjodheim.cellar.identity.internal.domain.RefreshToken;
import be.mjodheim.cellar.identity.internal.domain.User;

/**
 * Bidirectional mapper between Identity domain objects and JPA entities.
 */
final class IdentityPersistenceMapper {

    private IdentityPersistenceMapper() {}

    /**
     * Converts a user aggregate to its JPA representation.
     *
     * @param user domain user
     * @return persistence entity
     */
    static UserEntity toEntity(User user) {
        return new UserEntity(user.id(), user.email(), user.displayName(), user.passwordHash(), user.role(),
                user.enabled(), user.createdAt(), user.updatedAt(), user.deletedAt());
    }

    /**
     * Rehydrates a user aggregate from persisted values.
     *
     * @param entity persistence entity
     * @return domain user
     */
    static User toDomain(UserEntity entity) {
        return User.rehydrate(entity.id(), entity.email(), entity.displayName(), entity.passwordHash(), entity.role(),
                entity.enabled(), entity.createdAt(), entity.updatedAt(), entity.deletedAt());
    }

    /**
     * Converts a refresh token to its JPA representation.
     *
     * @param token domain refresh token
     * @return persistence entity
     */
    static RefreshTokenEntity toEntity(RefreshToken token) {
        return new RefreshTokenEntity(token.id(), token.userId(), token.tokenHash(), token.expiresAt(),
                token.createdAt(), token.revokedAt());
    }

    /**
     * Rehydrates a refresh token from persisted values.
     *
     * @param entity persistence entity
     * @return domain refresh token
     */
    static RefreshToken toDomain(RefreshTokenEntity entity) {
        return RefreshToken.rehydrate(entity.id(), entity.userId(), entity.tokenHash(), entity.expiresAt(),
                entity.createdAt(), entity.revokedAt());
    }
}

package be.mjodheim.cellar.identity.internal.adapter.out.persistence;

import be.mjodheim.cellar.identity.internal.domain.RefreshToken;
import be.mjodheim.cellar.identity.internal.domain.User;

final class IdentityPersistenceMapper {

    private IdentityPersistenceMapper() {}

    static UserEntity toEntity(User user) {
        return new UserEntity(user.id(), user.email(), user.displayName(), user.passwordHash(), user.role(),
                user.enabled(), user.createdAt(), user.updatedAt(), user.deletedAt());
    }

    static User toDomain(UserEntity entity) {
        return User.rehydrate(entity.id(), entity.email(), entity.displayName(), entity.passwordHash(), entity.role(),
                entity.enabled(), entity.createdAt(), entity.updatedAt(), entity.deletedAt());
    }

    static RefreshTokenEntity toEntity(RefreshToken token) {
        return new RefreshTokenEntity(token.id(), token.userId(), token.tokenHash(), token.expiresAt(),
                token.createdAt(), token.revokedAt());
    }

    static RefreshToken toDomain(RefreshTokenEntity entity) {
        return RefreshToken.rehydrate(entity.id(), entity.userId(), entity.tokenHash(), entity.expiresAt(),
                entity.createdAt(), entity.revokedAt());
    }
}

package be.mjodheim.cellar.identity.internal.adapter.out.persistence;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "refresh_token")
class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "token_hash", nullable = false, length = 64)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    protected RefreshTokenEntity() {}

    RefreshTokenEntity(Long id, Long userId, String tokenHash, Instant expiresAt, Instant createdAt, Instant revokedAt) {
        this.id=id; this.userId=userId; this.tokenHash=tokenHash; this.expiresAt=expiresAt;
        this.createdAt=createdAt; this.revokedAt=revokedAt;
    }

    Long id(){return id;} Long userId(){return userId;} String tokenHash(){return tokenHash;}
    Instant expiresAt(){return expiresAt;} Instant createdAt(){return createdAt;} Instant revokedAt(){return revokedAt;}
}

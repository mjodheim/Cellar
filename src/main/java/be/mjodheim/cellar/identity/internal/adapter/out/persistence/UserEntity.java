package be.mjodheim.cellar.identity.internal.adapter.out.persistence;

import be.mjodheim.cellar.identity.internal.domain.Role;
import jakarta.persistence.*;

import java.time.Instant;

/**
 * JPA representation of a Cellar user account.
 */
@Entity
@Table(name = "app_user")
class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 254)
    private String email;

    @Column(name = "display_name", nullable = false, length = 150)
    private String displayName;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false)
    private boolean enabled;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    /** Constructor required by JPA. */
    protected UserEntity() {}

    /**
     * Builds a user persistence entity from explicit stored values.
     */
    UserEntity(Long id, String email, String displayName, String passwordHash, Role role,
               boolean enabled, Instant createdAt, Instant updatedAt, Instant deletedAt) {
        this.id=id; this.email=email; this.displayName=displayName; this.passwordHash=passwordHash;
        this.role=role; this.enabled=enabled; this.createdAt=createdAt; this.updatedAt=updatedAt; this.deletedAt=deletedAt;
    }

    Long id(){return id;}
    String email(){return email;}
    String displayName(){return displayName;}
    String passwordHash(){return passwordHash;}
    Role role(){return role;}
    boolean enabled(){return enabled;}
    Instant createdAt(){return createdAt;}
    Instant updatedAt(){return updatedAt;}
    Instant deletedAt(){return deletedAt;}
}

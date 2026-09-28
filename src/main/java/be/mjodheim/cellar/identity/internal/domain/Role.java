package be.mjodheim.cellar.identity.internal.domain;

/**
 * Functional roles supported by Cellar.
 *
 * <p>{@link #USER} represents an authenticated operator, while {@link #ADMIN}
 * grants access to administrative catalogue and inventory operations.</p>
 */
public enum Role {
    USER,
    ADMIN
}

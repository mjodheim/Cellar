package be.mjodheim.cellar.catalog.internal.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * Business representation of a product offered in the Mjödheim catalogue.
 *
 * <p>This class deliberately contains no JPA or HTTP concerns. It protects the
 * product invariants and is persisted through a dedicated adapter.</p>
 */
public final class Product {

    private final Long id;

    private String name;
    private ProductType type;
    private String description;
    private int volumeMl;
    private BigDecimal price;
    private boolean active;

    private final Instant createdAt;
    private Instant updatedAt;

    private Product(
            Long id,
            String name,
            ProductType type,
            String description,
            int volumeMl,
            BigDecimal price,
            boolean active,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
        this.active = active;

        applyDetails(
                name,
                type,
                description,
                volumeMl,
                price
        );
    }

    public static Product create(
            String name,
            ProductType type,
            String description,
            int volumeMl,
            BigDecimal price,
            Instant now
    ) {
        Objects.requireNonNull(now);

        return new Product(
                null,
                name,
                type,
                description,
                volumeMl,
                price,
                true,
                now,
                now
        );
    }

    public static Product rehydrate(
            Long id,
            String name,
            ProductType type,
            String description,
            int volumeMl,
            BigDecimal price,
            boolean active,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new Product(
                id,
                name,
                type,
                description,
                volumeMl,
                price,
                active,
                createdAt,
                updatedAt
        );
    }

    public void changeDetails(
            String name,
            ProductType type,
            String description,
            int volumeMl,
            BigDecimal price,
            Instant now
    ) {
        applyDetails(
                name,
                type,
                description,
                volumeMl,
                price
        );

        this.updatedAt = Objects.requireNonNull(now);
    }

    public void deactivate(Instant now) {
        if (!active) {
            return;
        }

        this.active = false;
        this.updatedAt = Objects.requireNonNull(now);
    }

    private void applyDetails(
            String name,
            ProductType type,
            String description,
            int volumeMl,
            BigDecimal price
    ) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name is required");
        }

        if (type == null) {
            throw new IllegalArgumentException("Product type is required");
        }

        if (volumeMl <= 0) {
            throw new IllegalArgumentException("Product volume must be greater than zero");
        }

        if (price == null || price.signum() < 0) {
            throw new IllegalArgumentException("Product price cannot be negative");
        }

        this.name = name.trim();
        this.type = type;
        this.description = description;
        this.volumeMl = volumeMl;
        this.price = price;
    }

    public Long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public ProductType type() {
        return type;
    }

    public String description() {
        return description;
    }

    public int volumeMl() {
        return volumeMl;
    }

    public BigDecimal price() {
        return price;
    }

    public boolean active() {
        return active;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
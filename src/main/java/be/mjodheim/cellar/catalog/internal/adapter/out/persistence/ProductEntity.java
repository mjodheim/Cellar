package be.mjodheim.cellar.catalog.internal.adapter.out.persistence;

import be.mjodheim.cellar.catalog.internal.domain.ProductType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "product")
class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ProductType type;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "volume_ml", nullable = false)
    private int volumeMl;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ProductEntity() {
    }

    ProductEntity(
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
        this.name = name;
        this.type = type;
        this.description = description;
        this.volumeMl = volumeMl;
        this.price = price;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    Long id() {
        return id;
    }

    String name() {
        return name;
    }

    ProductType type() {
        return type;
    }

    String description() {
        return description;
    }

    int volumeMl() {
        return volumeMl;
    }

    BigDecimal price() {
        return price;
    }

    boolean active() {
        return active;
    }

    Instant createdAt() {
        return createdAt;
    }

    Instant updatedAt() {
        return updatedAt;
    }
}

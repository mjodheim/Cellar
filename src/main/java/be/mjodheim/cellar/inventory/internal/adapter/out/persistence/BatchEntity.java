package be.mjodheim.cellar.inventory.internal.adapter.out.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

/**
 * JPA representation of a physical inventory batch.
 */
@Entity
@Table(name = "batch")
class BatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "lot_number", nullable = false, length = 100)
    private String lotNumber;

    @Column(name = "received_quantity", nullable = false)
    private int receivedQuantity;

    @Column(name = "quantity_on_hand", nullable = false)
    private int quantityOnHand;

    @Column(name = "quantity_reserved", nullable = false)
    private int quantityReserved;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    @Column(name = "expires_on")
    private LocalDate expiresOn;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    /** Constructor required by JPA. */
    protected BatchEntity() {}

    /**
     * Builds a batch persistence entity from explicit stored values.
     */
    BatchEntity(Long id, Long productId, String lotNumber, int receivedQuantity, int quantityOnHand,
                int quantityReserved, Instant receivedAt, LocalDate expiresOn, Instant createdAt,
                Instant updatedAt, Instant deletedAt) {
        this.id = id;
        this.productId = productId;
        this.lotNumber = lotNumber;
        this.receivedQuantity = receivedQuantity;
        this.quantityOnHand = quantityOnHand;
        this.quantityReserved = quantityReserved;
        this.receivedAt = receivedAt;
        this.expiresOn = expiresOn;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    Long id(){ return id; }
    Long productId(){ return productId; }
    String lotNumber(){ return lotNumber; }
    int receivedQuantity(){ return receivedQuantity; }
    int quantityOnHand(){ return quantityOnHand; }
    int quantityReserved(){ return quantityReserved; }
    Instant receivedAt(){ return receivedAt; }
    LocalDate expiresOn(){ return expiresOn; }
    Instant createdAt(){ return createdAt; }
    Instant updatedAt(){ return updatedAt; }
    Instant deletedAt(){ return deletedAt; }
}

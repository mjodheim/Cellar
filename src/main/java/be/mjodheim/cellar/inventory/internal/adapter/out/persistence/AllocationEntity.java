package be.mjodheim.cellar.inventory.internal.adapter.out.persistence;

import be.mjodheim.cellar.inventory.internal.domain.AllocationStatus;
import jakarta.persistence.*;

import java.time.Instant;

/**
 * JPA representation of a stock allocation between an order line and a batch.
 */
@Entity
@Table(name = "allocation")
class AllocationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_line_id", nullable = false)
    private Long orderLineId;

    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AllocationStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** Constructor required by JPA. */
    protected AllocationEntity() {}

    /**
     * Builds an allocation entity from explicit persisted values.
     */
    AllocationEntity(Long id, Long orderLineId, Long batchId, int quantity, AllocationStatus status,
                     Instant createdAt, Instant updatedAt) {
        this.id=id; this.orderLineId=orderLineId; this.batchId=batchId; this.quantity=quantity;
        this.status=status; this.createdAt=createdAt; this.updatedAt=updatedAt;
    }

    Long id(){return id;}
    Long orderLineId(){return orderLineId;}
    Long batchId(){return batchId;}
    int quantity(){return quantity;}
    AllocationStatus status(){return status;}
    Instant createdAt(){return createdAt;}
    Instant updatedAt(){return updatedAt;}
}

package be.mjodheim.cellar.inventory.internal.adapter.out.persistence;

import be.mjodheim.cellar.inventory.internal.domain.StockMovementType;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "stock_movement")
class StockMovementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StockMovementType type;

    @Column(nullable = false)
    private int quantity;

    @Column(length = 150)
    private String reference;

    @Column(columnDefinition = "text")
    private String note;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected StockMovementEntity() {}

    StockMovementEntity(Long id, Long batchId, StockMovementType type, int quantity, String reference,
                        String note, Instant occurredAt, Instant createdAt) {
        this.id=id; this.batchId=batchId; this.type=type; this.quantity=quantity; this.reference=reference;
        this.note=note; this.occurredAt=occurredAt; this.createdAt=createdAt;
    }

    Long id(){return id;} Long batchId(){return batchId;} StockMovementType type(){return type;}
    int quantity(){return quantity;} String reference(){return reference;} String note(){return note;}
    Instant occurredAt(){return occurredAt;} Instant createdAt(){return createdAt;}
}

package be.mjodheim.cellar.inventory.internal.domain;

import java.time.Instant;
import java.util.Objects;

/**
 * Reservation link between an order line and a physical batch.
 *
 * <p>An allocation starts as RESERVED and can then be released or consumed.
 * It is intentionally separate from physical stock movements because a
 * reservation does not change the physical quantity on hand.</p>
 */
public final class Allocation {

    private final Long id;
    private final Long orderLineId;
    private final Long batchId;
    private final int quantity;
    private final Instant createdAt;

    private AllocationStatus status;
    private Instant updatedAt;

    private Allocation(
            Long id,
            Long orderLineId,
            Long batchId,
            int quantity,
            AllocationStatus status,
            Instant createdAt,
            Instant updatedAt
    ) {
        if (orderLineId == null || orderLineId <= 0) {
            throw new IllegalArgumentException("Order line id must be positive");
        }
        if (batchId == null || batchId <= 0) {
            throw new IllegalArgumentException("Batch id must be positive");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Allocation quantity must be greater than zero");
        }

        this.id = id;
        this.orderLineId = orderLineId;
        this.batchId = batchId;
        this.quantity = quantity;
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }

    public static Allocation reserve(Long orderLineId, Long batchId, int quantity, Instant now) {
        return new Allocation(
                null,
                orderLineId,
                batchId,
                quantity,
                AllocationStatus.RESERVED,
                now,
                now
        );
    }

    public static Allocation rehydrate(
            Long id,
            Long orderLineId,
            Long batchId,
            int quantity,
            AllocationStatus status,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new Allocation(id, orderLineId, batchId, quantity, status, createdAt, updatedAt);
    }

    public void release(Instant now) {
        requireReserved();
        status = AllocationStatus.RELEASED;
        updatedAt = Objects.requireNonNull(now);
    }

    public void consume(Instant now) {
        requireReserved();
        status = AllocationStatus.CONSUMED;
        updatedAt = Objects.requireNonNull(now);
    }

    private void requireReserved() {
        if (status != AllocationStatus.RESERVED) {
            throw new IllegalStateException("Only a reserved allocation can change state");
        }
    }

    public Long id() { return id; }
    public Long orderLineId() { return orderLineId; }
    public Long batchId() { return batchId; }
    public int quantity() { return quantity; }
    public AllocationStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}

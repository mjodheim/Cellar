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
        if (orderLineId == null || orderLineId <= 0) throw new IllegalArgumentException("Order line id must be positive");
        if (batchId == null || batchId <= 0) throw new IllegalArgumentException("Batch id must be positive");
        if (quantity <= 0) throw new IllegalArgumentException("Allocation quantity must be greater than zero");

        this.id = id;
        this.orderLineId = orderLineId;
        this.batchId = batchId;
        this.quantity = quantity;
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }

    /**
     * Creates a new reserved allocation.
     *
     * @param orderLineId order-line identifier
     * @param batchId batch identifier
     * @param quantity reserved quantity
     * @param now creation timestamp
     * @return new allocation
     */
    public static Allocation reserve(Long orderLineId, Long batchId, int quantity, Instant now) {
        return new Allocation(null, orderLineId, batchId, quantity, AllocationStatus.RESERVED, now, now);
    }

    /**
     * Rebuilds an allocation from persisted state.
     *
     * @param id persisted identifier
     * @param orderLineId order-line identifier
     * @param batchId batch identifier
     * @param quantity allocated quantity
     * @param status allocation status
     * @param createdAt creation timestamp
     * @param updatedAt last modification timestamp
     * @return rehydrated allocation
     */
    public static Allocation rehydrate(
            Long id, Long orderLineId, Long batchId, int quantity,
            AllocationStatus status, Instant createdAt, Instant updatedAt
    ) {
        return new Allocation(id, orderLineId, batchId, quantity, status, createdAt, updatedAt);
    }

    /**
     * Releases a reserved allocation.
     *
     * @param now modification timestamp
     */
    public void release(Instant now) {
        requireReserved();
        status = AllocationStatus.RELEASED;
        updatedAt = Objects.requireNonNull(now);
    }

    /**
     * Marks a reserved allocation as consumed by shipment.
     *
     * @param now modification timestamp
     */
    public void consume(Instant now) {
        requireReserved();
        status = AllocationStatus.CONSUMED;
        updatedAt = Objects.requireNonNull(now);
    }

    private void requireReserved() {
        if (status != AllocationStatus.RESERVED) throw new IllegalStateException("Only a reserved allocation can change state");
    }

    /** @return persisted identifier, or {@code null} before persistence */
    public Long id() { return id; }
    /** @return owning order-line identifier */
    public Long orderLineId() { return orderLineId; }
    /** @return allocated batch identifier */
    public Long batchId() { return batchId; }
    /** @return allocated quantity */
    public int quantity() { return quantity; }
    /** @return current allocation status */
    public AllocationStatus status() { return status; }
    /** @return creation timestamp */
    public Instant createdAt() { return createdAt; }
    /** @return last modification timestamp */
    public Instant updatedAt() { return updatedAt; }
}

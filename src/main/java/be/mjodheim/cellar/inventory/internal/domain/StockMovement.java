package be.mjodheim.cellar.inventory.internal.domain;

import java.time.Instant;
import java.util.Objects;

/**
 * Immutable stock-ledger entry explaining a physical stock variation.
 *
 * <p>The direction of the movement is expressed by {@link StockMovementType};
 * quantities are therefore always strictly positive.</p>
 */
public final class StockMovement {

    private final Long id;
    private final Long batchId;
    private final StockMovementType type;
    private final int quantity;
    private final String reference;
    private final String note;
    private final Instant occurredAt;
    private final Instant createdAt;

    private StockMovement(Long id, Long batchId, StockMovementType type, int quantity, String reference,
                          String note, Instant occurredAt, Instant createdAt) {
        if (batchId == null || batchId <= 0) throw new IllegalArgumentException("Batch id must be positive");
        if (quantity <= 0) throw new IllegalArgumentException("Movement quantity must be greater than zero");
        this.id=id; this.batchId=batchId; this.type=Objects.requireNonNull(type, "Movement type is required");
        this.quantity=quantity; this.reference=normalize(reference); this.note=normalize(note);
        this.occurredAt=Objects.requireNonNull(occurredAt, "Movement date is required");
        this.createdAt=Objects.requireNonNull(createdAt, "Creation date is required");
    }

    /**
     * Records a new stock movement.
     *
     * @param batchId affected batch identifier
     * @param type movement type
     * @param quantity positive movement quantity
     * @param reference optional business reference
     * @param note optional explanatory note
     * @param occurredAt business occurrence timestamp
     * @param now creation timestamp
     * @return new stock movement
     */
    public static StockMovement record(Long batchId, StockMovementType type, int quantity, String reference,
                                       String note, Instant occurredAt, Instant now) {
        return new StockMovement(null, batchId, type, quantity, reference, note, occurredAt, now);
    }

    /**
     * Rebuilds a stock movement from persistence.
     *
     * @param id persisted identifier
     * @param batchId affected batch identifier
     * @param type movement type
     * @param quantity movement quantity
     * @param reference optional reference
     * @param note optional note
     * @param occurredAt business occurrence timestamp
     * @param createdAt creation timestamp
     * @return rehydrated movement
     */
    public static StockMovement rehydrate(Long id, Long batchId, StockMovementType type, int quantity,
                                          String reference, String note, Instant occurredAt, Instant createdAt) {
        return new StockMovement(id, batchId, type, quantity, reference, note, occurredAt, createdAt);
    }

    private static String normalize(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /** @return persisted identifier, or {@code null} before persistence */
    public Long id() { return id; }
    /** @return affected batch identifier */
    public Long batchId() { return batchId; }
    /** @return movement type */
    public StockMovementType type() { return type; }
    /** @return positive movement quantity */
    public int quantity() { return quantity; }
    /** @return optional business reference */
    public String reference() { return reference; }
    /** @return optional explanatory note */
    public String note() { return note; }
    /** @return business occurrence timestamp */
    public Instant occurredAt() { return occurredAt; }
    /** @return creation timestamp */
    public Instant createdAt() { return createdAt; }
}

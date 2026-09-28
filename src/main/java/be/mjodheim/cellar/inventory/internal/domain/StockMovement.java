package be.mjodheim.cellar.inventory.internal.domain;

import java.time.Instant;
import java.util.Objects;

public final class StockMovement {

    private final Long id;
    private final Long batchId;
    private final StockMovementType type;
    private final int quantity;
    private final String reference;
    private final String note;
    private final Instant occurredAt;
    private final Instant createdAt;

    private StockMovement(
            Long id,
            Long batchId,
            StockMovementType type,
            int quantity,
            String reference,
            String note,
            Instant occurredAt,
            Instant createdAt
    ) {
        if (batchId == null || batchId <= 0) {
            throw new IllegalArgumentException("Batch id must be positive");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Movement quantity must be greater than zero");
        }

        this.id = id;
        this.batchId = batchId;
        this.type = Objects.requireNonNull(type, "Movement type is required");
        this.quantity = quantity;
        this.reference = normalize(reference);
        this.note = normalize(note);
        this.occurredAt = Objects.requireNonNull(occurredAt, "Movement date is required");
        this.createdAt = Objects.requireNonNull(createdAt, "Creation date is required");
    }

    public static StockMovement record(
            Long batchId,
            StockMovementType type,
            int quantity,
            String reference,
            String note,
            Instant occurredAt,
            Instant now
    ) {
        return new StockMovement(null, batchId, type, quantity, reference, note, occurredAt, now);
    }

    public static StockMovement rehydrate(
            Long id,
            Long batchId,
            StockMovementType type,
            int quantity,
            String reference,
            String note,
            Instant occurredAt,
            Instant createdAt
    ) {
        return new StockMovement(id, batchId, type, quantity, reference, note, occurredAt, createdAt);
    }

    private static String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public Long id() { return id; }
    public Long batchId() { return batchId; }
    public StockMovementType type() { return type; }
    public int quantity() { return quantity; }
    public String reference() { return reference; }
    public String note() { return note; }
    public Instant occurredAt() { return occurredAt; }
    public Instant createdAt() { return createdAt; }
}

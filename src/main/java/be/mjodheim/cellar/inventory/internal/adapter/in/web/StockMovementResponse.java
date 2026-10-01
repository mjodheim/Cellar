package be.mjodheim.cellar.inventory.internal.adapter.in.web;

import be.mjodheim.cellar.inventory.internal.domain.StockMovement;
import be.mjodheim.cellar.inventory.internal.domain.StockMovementType;

import java.time.Instant;

/**
 * HTTP representation of an immutable stock-movement ledger entry.
 *
 * @param id movement identifier
 * @param batchId affected batch identifier
 * @param type movement type
 * @param quantity positive movement quantity
 * @param reference optional external or business reference
 * @param note optional explanatory note
 * @param occurredAt business occurrence timestamp
 * @param createdAt persistence creation timestamp
 */
record StockMovementResponse(
        Long id,
        Long batchId,
        StockMovementType type,
        int quantity,
        String reference,
        String note,
        Instant occurredAt,
        Instant createdAt
) {
    /**
     * Maps a domain movement to its HTTP representation.
     *
     * @param movement domain stock movement
     * @return immutable response projection
     */
    static StockMovementResponse from(StockMovement movement) {
        return new StockMovementResponse(
                movement.id(),
                movement.batchId(),
                movement.type(),
                movement.quantity(),
                movement.reference(),
                movement.note(),
                movement.occurredAt(),
                movement.createdAt()
        );
    }
}

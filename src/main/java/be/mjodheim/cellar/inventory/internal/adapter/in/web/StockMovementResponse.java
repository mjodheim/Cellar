package be.mjodheim.cellar.inventory.internal.adapter.in.web;

import be.mjodheim.cellar.inventory.internal.domain.StockMovement;
import be.mjodheim.cellar.inventory.internal.domain.StockMovementType;

import java.time.Instant;

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

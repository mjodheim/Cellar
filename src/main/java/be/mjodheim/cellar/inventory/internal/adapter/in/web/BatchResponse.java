package be.mjodheim.cellar.inventory.internal.adapter.in.web;

import be.mjodheim.cellar.inventory.internal.domain.Batch;

import java.time.Instant;
import java.time.LocalDate;

record BatchResponse(
        Long id,
        Long productId,
        String lotNumber,
        int receivedQuantity,
        int quantityOnHand,
        int quantityReserved,
        int availableQuantity,
        Instant receivedAt,
        LocalDate expiresOn,
        Instant createdAt,
        Instant updatedAt
) {
    static BatchResponse from(Batch batch) {
        return new BatchResponse(
                batch.id(),
                batch.productId(),
                batch.lotNumber(),
                batch.receivedQuantity(),
                batch.quantityOnHand(),
                batch.quantityReserved(),
                batch.availableQuantity(),
                batch.receivedAt(),
                batch.expiresOn(),
                batch.createdAt(),
                batch.updatedAt()
        );
    }
}

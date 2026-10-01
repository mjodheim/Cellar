package be.mjodheim.cellar.inventory.internal.adapter.in.web;

import be.mjodheim.cellar.inventory.internal.domain.Batch;

import java.time.Instant;
import java.time.LocalDate;

/**
 * HTTP representation of a physical stock batch.
 *
 * @param id batch identifier
 * @param productId related catalogue product identifier
 * @param lotNumber lot reference
 * @param receivedQuantity original received quantity
 * @param quantityOnHand current physical quantity
 * @param quantityReserved quantity reserved for orders
 * @param availableQuantity quantity currently available for allocation
 * @param receivedAt reception timestamp
 * @param expiresOn optional expiration date
 * @param createdAt creation timestamp
 * @param updatedAt last modification timestamp
 */
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
    /**
     * Maps a domain batch to its HTTP representation.
     *
     * @param batch domain batch
     * @return immutable response projection
     */
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

package be.mjodheim.cellar.inventory.internal.adapter.out.persistence;

import be.mjodheim.cellar.inventory.internal.domain.*;

/**
 * Bidirectional mapper between Inventory domain objects and JPA entities.
 */
final class InventoryPersistenceMapper {

    private InventoryPersistenceMapper() {}

    /**
     * Converts a batch aggregate to a persistence entity.
     *
     * @param batch domain batch
     * @return persistence entity
     */
    static BatchEntity toEntity(Batch batch) {
        return new BatchEntity(batch.id(), batch.productId(), batch.lotNumber(), batch.receivedQuantity(),
                batch.quantityOnHand(), batch.quantityReserved(), batch.receivedAt(), batch.expiresOn(),
                batch.createdAt(), batch.updatedAt(), batch.deletedAt());
    }

    /**
     * Rehydrates a batch aggregate from persisted values.
     *
     * @param entity persistence entity
     * @return domain batch
     */
    static Batch toDomain(BatchEntity entity) {
        return Batch.rehydrate(entity.id(), entity.productId(), entity.lotNumber(), entity.receivedQuantity(),
                entity.quantityOnHand(), entity.quantityReserved(), entity.receivedAt(), entity.expiresOn(),
                entity.createdAt(), entity.updatedAt(), entity.deletedAt());
    }

    /**
     * Converts a stock movement to a persistence entity.
     *
     * @param movement domain movement
     * @return persistence entity
     */
    static StockMovementEntity toEntity(StockMovement movement) {
        return new StockMovementEntity(movement.id(), movement.batchId(), movement.type(), movement.quantity(), movement.reference(),
                movement.note(), movement.occurredAt(), movement.createdAt());
    }

    /**
     * Rehydrates a stock movement from persisted values.
     *
     * @param entity persistence entity
     * @return domain movement
     */
    static StockMovement toDomain(StockMovementEntity entity) {
        return StockMovement.rehydrate(entity.id(), entity.batchId(), entity.type(), entity.quantity(), entity.reference(),
                entity.note(), entity.occurredAt(), entity.createdAt());
    }

    /**
     * Converts an allocation to a persistence entity.
     *
     * @param allocation domain allocation
     * @return persistence entity
     */
    static AllocationEntity toEntity(Allocation allocation) {
        return new AllocationEntity(allocation.id(), allocation.orderLineId(), allocation.batchId(), allocation.quantity(), allocation.status(),
                allocation.createdAt(), allocation.updatedAt());
    }

    /**
     * Rehydrates an allocation from persisted values.
     *
     * @param entity persistence entity
     * @return domain allocation
     */
    static Allocation toDomain(AllocationEntity entity) {
        return Allocation.rehydrate(entity.id(), entity.orderLineId(), entity.batchId(), entity.quantity(), entity.status(),
                entity.createdAt(), entity.updatedAt());
    }
}

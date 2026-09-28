package be.mjodheim.cellar.inventory.internal.adapter.out.persistence;

import be.mjodheim.cellar.inventory.internal.domain.*;

final class InventoryPersistenceMapper {

    private InventoryPersistenceMapper() {}

    static BatchEntity toEntity(Batch b) {
        return new BatchEntity(b.id(), b.productId(), b.lotNumber(), b.receivedQuantity(),
                b.quantityOnHand(), b.quantityReserved(), b.receivedAt(), b.expiresOn(),
                b.createdAt(), b.updatedAt(), b.deletedAt());
    }

    static Batch toDomain(BatchEntity e) {
        return Batch.rehydrate(e.id(), e.productId(), e.lotNumber(), e.receivedQuantity(),
                e.quantityOnHand(), e.quantityReserved(), e.receivedAt(), e.expiresOn(),
                e.createdAt(), e.updatedAt(), e.deletedAt());
    }

    static StockMovementEntity toEntity(StockMovement m) {
        return new StockMovementEntity(m.id(), m.batchId(), m.type(), m.quantity(), m.reference(),
                m.note(), m.occurredAt(), m.createdAt());
    }

    static StockMovement toDomain(StockMovementEntity e) {
        return StockMovement.rehydrate(e.id(), e.batchId(), e.type(), e.quantity(), e.reference(),
                e.note(), e.occurredAt(), e.createdAt());
    }

    static AllocationEntity toEntity(Allocation a) {
        return new AllocationEntity(a.id(), a.orderLineId(), a.batchId(), a.quantity(), a.status(),
                a.createdAt(), a.updatedAt());
    }

    static Allocation toDomain(AllocationEntity e) {
        return Allocation.rehydrate(e.id(), e.orderLineId(), e.batchId(), e.quantity(), e.status(),
                e.createdAt(), e.updatedAt());
    }
}

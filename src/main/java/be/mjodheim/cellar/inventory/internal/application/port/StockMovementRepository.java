package be.mjodheim.cellar.inventory.internal.application.port;

import be.mjodheim.cellar.inventory.internal.domain.StockMovement;

import java.util.List;

/**
 * Outbound persistence port for the stock-movement ledger.
 */
public interface StockMovementRepository {

    /**
     * Persists a stock movement.
     *
     * @param movement movement to save
     * @return persisted movement
     */
    StockMovement save(StockMovement movement);

    /**
     * Lists movements belonging to a batch.
     *
     * @param batchId batch identifier
     * @return movements in chronological repository order
     */
    List<StockMovement> findByBatchId(Long batchId);
}

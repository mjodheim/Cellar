package be.mjodheim.cellar.inventory.internal.application;

import be.mjodheim.cellar.inventory.internal.application.port.BatchRepository;
import be.mjodheim.cellar.inventory.internal.application.port.StockMovementRepository;
import be.mjodheim.cellar.inventory.internal.domain.Batch;
import be.mjodheim.cellar.inventory.internal.domain.StockMovement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Read-only Inventory use cases for batches and their stock-movement history.
 */
@Service
public class InventoryQueryService {

    private final BatchRepository batchRepository;
    private final StockMovementRepository movementRepository;

    /**
     * Creates the inventory query service.
     *
     * @param batchRepository batch persistence boundary
     * @param movementRepository movement persistence boundary
     */
    public InventoryQueryService(BatchRepository batchRepository, StockMovementRepository movementRepository) {
        this.batchRepository = batchRepository;
        this.movementRepository = movementRepository;
    }

    /**
     * Retrieves a non-deleted batch by identifier.
     *
     * @param id batch identifier
     * @return matching batch
     * @throws BatchNotFoundException when the batch does not exist
     */
    @Transactional(readOnly = true)
    public Batch findBatchById(Long id) {
        return batchRepository.findById(id).orElseThrow(() -> new BatchNotFoundException(id));
    }

    /**
     * Lists available batches for a product in FEFO order.
     *
     * @param productId product identifier
     * @return available batches
     */
    @Transactional(readOnly = true)
    public List<Batch> findBatchesForProduct(Long productId) {
        return batchRepository.findByProductIdFefo(productId);
    }

    /**
     * Lists stock movements for an existing batch.
     *
     * @param batchId batch identifier
     * @return ordered stock movements
     * @throws BatchNotFoundException when the batch does not exist
     */
    @Transactional(readOnly = true)
    public List<StockMovement> findMovements(Long batchId) {
        findBatchById(batchId);
        return movementRepository.findByBatchId(batchId);
    }
}

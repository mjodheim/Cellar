package be.mjodheim.cellar.inventory.internal.application;

import be.mjodheim.cellar.inventory.internal.application.port.BatchRepository;
import be.mjodheim.cellar.inventory.internal.application.port.StockMovementRepository;
import be.mjodheim.cellar.inventory.internal.domain.Batch;
import be.mjodheim.cellar.inventory.internal.domain.StockMovement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
/**
 * Read-only Inventory use cases for batches and their stock-movement history.
 */
public class InventoryQueryService {

    private final BatchRepository batchRepository;
    private final StockMovementRepository movementRepository;

    public InventoryQueryService(BatchRepository batchRepository, StockMovementRepository movementRepository) {
        this.batchRepository = batchRepository;
        this.movementRepository = movementRepository;
    }

    @Transactional(readOnly = true)
    public Batch findBatchById(Long id) {
        return batchRepository.findById(id).orElseThrow(() -> new BatchNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Batch> findBatchesForProduct(Long productId) {
        return batchRepository.findByProductIdFefo(productId);
    }

    @Transactional(readOnly = true)
    public List<StockMovement> findMovements(Long batchId) {
        findBatchById(batchId);
        return movementRepository.findByBatchId(batchId);
    }
}

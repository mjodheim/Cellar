package be.mjodheim.cellar.inventory.internal.application;

import be.mjodheim.cellar.inventory.InventoryOperations;
import org.springframework.stereotype.Service;

/**
 * Internal implementation of the public Inventory module API used by Ordering.
 */
@Service
class InventoryOperationsService implements InventoryOperations {

    private final AllocateStockService allocateStockService;
    private final AllocationLifecycleService lifecycleService;

    InventoryOperationsService(
            AllocateStockService allocateStockService,
            AllocationLifecycleService lifecycleService
    ) {
        this.allocateStockService = allocateStockService;
        this.lifecycleService = lifecycleService;
    }

    /** {@inheritDoc} */
    @Override
    public void allocate(Long orderLineId, Long productId, int quantity) {
        allocateStockService.allocateFefo(orderLineId, productId, quantity);
    }

    /** {@inheritDoc} */
    @Override
    public void release(Long orderLineId) {
        lifecycleService.releaseForOrderLine(orderLineId);
    }

    /** {@inheritDoc} */
    @Override
    public void consume(Long orderLineId) {
        lifecycleService.consumeForOrderLine(orderLineId);
    }
}

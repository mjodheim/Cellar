package be.mjodheim.cellar.inventory.internal.application;

import be.mjodheim.cellar.inventory.internal.application.port.AllocationRepository;
import be.mjodheim.cellar.inventory.internal.application.port.BatchRepository;
import be.mjodheim.cellar.inventory.internal.application.port.StockMovementRepository;
import be.mjodheim.cellar.inventory.internal.domain.Allocation;
import be.mjodheim.cellar.inventory.internal.domain.AllocationStatus;
import be.mjodheim.cellar.inventory.internal.domain.Batch;
import be.mjodheim.cellar.inventory.internal.domain.StockMovement;
import be.mjodheim.cellar.inventory.internal.domain.StockMovementType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Releases or consumes stock allocations while keeping batches, allocations and
 * stock-movement history consistent in one transaction.
 */
@Service
public class AllocationLifecycleService {

    private final AllocationRepository allocationRepository;
    private final BatchRepository batchRepository;
    private final StockMovementRepository movementRepository;

    /**
     * Creates the allocation lifecycle service.
     *
     * @param allocationRepository allocation persistence boundary
     * @param batchRepository batch persistence boundary
     * @param movementRepository movement persistence boundary
     */
    public AllocationLifecycleService(
            AllocationRepository allocationRepository,
            BatchRepository batchRepository,
            StockMovementRepository movementRepository
    ) {
        this.allocationRepository = allocationRepository;
        this.batchRepository = batchRepository;
        this.movementRepository = movementRepository;
    }

    /**
     * Releases every reserved allocation for an order line.
     *
     * @param orderLineId order-line identifier
     */
    @Transactional
    public void releaseForOrderLine(Long orderLineId) {
        Instant now = Instant.now();

        for (Allocation allocation : activeAllocations(orderLineId)) {
            Batch batch = batchRepository.findByIdForUpdate(allocation.batchId())
                    .orElseThrow(() -> new BatchNotFoundException(allocation.batchId()));

            batch.release(allocation.quantity(), now);
            allocation.release(now);

            batchRepository.save(batch);
            allocationRepository.save(allocation);
        }
    }

    /**
     * Consumes every reserved allocation for an order line and records shipment movements.
     *
     * @param orderLineId order-line identifier
     */
    @Transactional
    public void consumeForOrderLine(Long orderLineId) {
        Instant now = Instant.now();

        for (Allocation allocation : activeAllocations(orderLineId)) {
            Batch batch = batchRepository.findByIdForUpdate(allocation.batchId())
                    .orElseThrow(() -> new BatchNotFoundException(allocation.batchId()));

            batch.shipReserved(allocation.quantity(), now);
            allocation.consume(now);

            batchRepository.save(batch);
            allocationRepository.save(allocation);
            movementRepository.save(StockMovement.record(
                    batch.id(),
                    StockMovementType.SHIPMENT,
                    allocation.quantity(),
                    "ORDER_LINE-" + orderLineId,
                    "Shipment of reserved stock",
                    now,
                    now
            ));
        }
    }

    private List<Allocation> activeAllocations(Long orderLineId) {
        return allocationRepository.findByOrderLineIdForUpdate(orderLineId).stream()
                .filter(a -> a.status() == AllocationStatus.RESERVED)
                .toList();
    }
}

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

@Service
public class AllocationLifecycleService {

    private final AllocationRepository allocationRepository;
    private final BatchRepository batchRepository;
    private final StockMovementRepository movementRepository;

    public AllocationLifecycleService(
            AllocationRepository allocationRepository,
            BatchRepository batchRepository,
            StockMovementRepository movementRepository
    ) {
        this.allocationRepository = allocationRepository;
        this.batchRepository = batchRepository;
        this.movementRepository = movementRepository;
    }

    @Transactional
    public void releaseForOrderLine(Long orderLineId) {
        Instant now = Instant.now();

        for (Allocation allocation : activeAllocations(orderLineId)) {
            Batch batch = batchRepository.findById(allocation.batchId())
                    .orElseThrow(() -> new BatchNotFoundException(allocation.batchId()));

            batch.release(allocation.quantity(), now);
            allocation.release(now);

            batchRepository.save(batch);
            allocationRepository.save(allocation);
        }
    }

    @Transactional
    public void consumeForOrderLine(Long orderLineId) {
        Instant now = Instant.now();

        for (Allocation allocation : activeAllocations(orderLineId)) {
            Batch batch = batchRepository.findById(allocation.batchId())
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
        return allocationRepository.findByOrderLineId(orderLineId).stream()
                .filter(a -> a.status() == AllocationStatus.RESERVED)
                .toList();
    }
}

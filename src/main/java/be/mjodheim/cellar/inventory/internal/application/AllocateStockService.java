package be.mjodheim.cellar.inventory.internal.application;

import be.mjodheim.cellar.inventory.internal.application.port.AllocationRepository;
import be.mjodheim.cellar.inventory.internal.application.port.BatchRepository;
import be.mjodheim.cellar.inventory.internal.domain.Allocation;
import be.mjodheim.cellar.inventory.internal.domain.Batch;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class AllocateStockService {

    private final BatchRepository batchRepository;
    private final AllocationRepository allocationRepository;

    public AllocateStockService(BatchRepository batchRepository, AllocationRepository allocationRepository) {
        this.batchRepository = batchRepository;
        this.allocationRepository = allocationRepository;
    }

    @Transactional
    public List<Allocation> allocateFefo(Long orderLineId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        List<Batch> batches = batchRepository.findByProductIdFefo(productId);
        int totalAvailable = batches.stream().mapToInt(Batch::availableQuantity).sum();

        if (totalAvailable < quantity) {
            throw new InsufficientStockException(productId, quantity, totalAvailable);
        }

        int remaining = quantity;
        Instant now = Instant.now();
        List<Allocation> allocations = new ArrayList<>();

        for (Batch batch : batches) {
            if (remaining == 0) break;
            int allocated = Math.min(batch.availableQuantity(), remaining);
            if (allocated == 0) continue;

            batch.reserve(allocated, now);
            batchRepository.save(batch);
            allocations.add(allocationRepository.save(
                    Allocation.reserve(orderLineId, batch.id(), allocated, now)
            ));
            remaining -= allocated;
        }

        return List.copyOf(allocations);
    }
}

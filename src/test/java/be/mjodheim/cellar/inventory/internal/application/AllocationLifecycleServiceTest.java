package be.mjodheim.cellar.inventory.internal.application;

import be.mjodheim.cellar.inventory.internal.application.port.AllocationRepository;
import be.mjodheim.cellar.inventory.internal.application.port.BatchRepository;
import be.mjodheim.cellar.inventory.internal.application.port.StockMovementRepository;
import be.mjodheim.cellar.inventory.internal.domain.Allocation;
import be.mjodheim.cellar.inventory.internal.domain.AllocationStatus;
import be.mjodheim.cellar.inventory.internal.domain.Batch;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AllocationLifecycleServiceTest {

    @Mock AllocationRepository allocationRepository;
    @Mock BatchRepository batchRepository;
    @Mock StockMovementRepository movementRepository;

    @Test
    void shouldReleaseReservedAllocationAndBatchStock() {
        Batch batch = batch();
        batch.reserve(4, Instant.parse("2026-09-28T12:30:00Z"));
        Allocation allocation = Allocation.reserve(7L, 1L, 4, Instant.parse("2026-09-28T12:30:00Z"));

        when(allocationRepository.findByOrderLineId(7L)).thenReturn(List.of(allocation));
        when(batchRepository.findById(1L)).thenReturn(Optional.of(batch));

        new AllocationLifecycleService(allocationRepository, batchRepository, movementRepository)
                .releaseForOrderLine(7L);

        assertEquals(0, batch.quantityReserved());
        assertEquals(AllocationStatus.RELEASED, allocation.status());
    }

    @Test
    void shouldConsumeReservedAllocationAndCreateShipmentMovement() {
        Batch batch = batch();
        batch.reserve(4, Instant.parse("2026-09-28T12:30:00Z"));
        Allocation allocation = Allocation.reserve(7L, 1L, 4, Instant.parse("2026-09-28T12:30:00Z"));

        when(allocationRepository.findByOrderLineId(7L)).thenReturn(List.of(allocation));
        when(batchRepository.findById(1L)).thenReturn(Optional.of(batch));

        new AllocationLifecycleService(allocationRepository, batchRepository, movementRepository)
                .consumeForOrderLine(7L);

        assertEquals(6, batch.quantityOnHand());
        assertEquals(0, batch.quantityReserved());
        assertEquals(AllocationStatus.CONSUMED, allocation.status());
        verify(movementRepository).save(any());
    }

    private static Batch batch() {
        Instant now = Instant.parse("2026-09-28T12:00:00Z");
        return Batch.rehydrate(1L, 99L, "LOT", 10, 10, 0, now, null, now, now, null);
    }
}

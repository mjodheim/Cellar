package be.mjodheim.cellar.inventory.internal.application;

import be.mjodheim.cellar.inventory.InsufficientStockException;
import be.mjodheim.cellar.inventory.internal.application.port.AllocationRepository;
import be.mjodheim.cellar.inventory.internal.application.port.BatchRepository;
import be.mjodheim.cellar.inventory.internal.domain.Allocation;
import be.mjodheim.cellar.inventory.internal.domain.Batch;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AllocateStockServiceTest {

    @Mock BatchRepository batchRepository;
    @Mock AllocationRepository allocationRepository;

    @Test
    void shouldAllocateAcrossBatchesInRepositoryFefoOrder() {
        Batch first = batch(1L, "A", 3, LocalDate.of(2026, 10, 1));
        Batch second = batch(2L, "B", 5, LocalDate.of(2026, 11, 1));
        when(batchRepository.findByProductIdFefoForUpdate(99L)).thenReturn(List.of(first, second));
        when(batchRepository.save(any(Batch.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(allocationRepository.save(any(Allocation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AllocateStockService service = new AllocateStockService(batchRepository, allocationRepository);
        List<Allocation> allocations = service.allocateFefo(10L, 99L, 6);

        assertEquals(2, allocations.size());
        assertEquals(3, allocations.get(0).quantity());
        assertEquals(3, allocations.get(1).quantity());
        assertEquals(0, first.availableQuantity());
        assertEquals(2, second.availableQuantity());
    }

    @Test
    void shouldRejectWhenTotalStockIsInsufficient() {
        when(batchRepository.findByProductIdFefoForUpdate(99L)).thenReturn(List.of(batch(1L, "A", 2, null)));

        AllocateStockService service = new AllocateStockService(batchRepository, allocationRepository);

        assertThrows(InsufficientStockException.class, () -> service.allocateFefo(10L, 99L, 3));
        verify(allocationRepository, never()).save(any());
    }

    private static Batch batch(Long id, String lot, int quantity, LocalDate expiry) {
        Instant now = Instant.parse("2026-09-28T12:00:00Z");
        return Batch.rehydrate(id, 99L, lot, quantity, quantity, 0, now, expiry, now, now, null);
    }
}

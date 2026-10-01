package be.mjodheim.cellar.inventory.internal.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class AllocationTest {

    private static final Instant NOW = Instant.parse("2026-09-28T12:00:00Z");
    private static final Instant LATER = Instant.parse("2026-09-28T13:00:00Z");

    @Test
    void shouldCreateReservedAllocation() {
        Allocation allocation = Allocation.reserve(10L, 20L, 5, NOW);

        assertEquals(AllocationStatus.RESERVED, allocation.status());
        assertEquals(5, allocation.quantity());
    }

    @Test
    void shouldReleaseReservedAllocation() {
        Allocation allocation = Allocation.reserve(10L, 20L, 5, NOW);

        allocation.release(LATER);

        assertEquals(AllocationStatus.RELEASED, allocation.status());
        assertEquals(LATER, allocation.updatedAt());
    }

    @Test
    void shouldConsumeReservedAllocation() {
        Allocation allocation = Allocation.reserve(10L, 20L, 5, NOW);

        allocation.consume(LATER);

        assertEquals(AllocationStatus.CONSUMED, allocation.status());
    }

    @Test
    void shouldNotChangeTerminalAllocation() {
        Allocation allocation = Allocation.reserve(10L, 20L, 5, NOW);
        allocation.release(LATER);

        assertThrows(IllegalStateException.class, () -> allocation.consume(LATER));
    }
}

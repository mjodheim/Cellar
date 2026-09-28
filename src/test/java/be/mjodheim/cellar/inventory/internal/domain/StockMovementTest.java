package be.mjodheim.cellar.inventory.internal.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class StockMovementTest {

    private static final Instant NOW = Instant.parse("2026-09-28T12:00:00Z");

    @Test
    void shouldRecordMovement() {
        StockMovement movement = StockMovement.record(
                1L,
                StockMovementType.RECEIPT,
                25,
                "LOT-001",
                "Réception",
                NOW,
                NOW
        );

        assertEquals(1L, movement.batchId());
        assertEquals(StockMovementType.RECEIPT, movement.type());
        assertEquals(25, movement.quantity());
    }

    @Test
    void shouldRejectNonPositiveQuantity() {
        assertThrows(IllegalArgumentException.class, () -> StockMovement.record(
                1L,
                StockMovementType.RECEIPT,
                0,
                null,
                null,
                NOW,
                NOW
        ));
    }
}

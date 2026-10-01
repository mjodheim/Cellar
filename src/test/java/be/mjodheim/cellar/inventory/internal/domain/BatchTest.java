package be.mjodheim.cellar.inventory.internal.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BatchTest {

    private static final Instant NOW = Instant.parse("2026-09-28T12:00:00Z");
    private static final Instant LATER = Instant.parse("2026-09-28T13:00:00Z");

    @Test
    void shouldReceiveValidBatch() {
        Batch batch = Batch.receive(1L, "LOT-001", 100, NOW, LocalDate.of(2027, 1, 1), NOW);

        assertNull(batch.id());
        assertEquals(100, batch.quantityOnHand());
        assertEquals(0, batch.quantityReserved());
        assertEquals(100, batch.availableQuantity());
        assertFalse(batch.isDeleted());
    }

    @Test
    void shouldRejectInvalidInitialQuantity() {
        assertThrows(IllegalArgumentException.class,
                () -> Batch.receive(1L, "LOT-001", 0, NOW, null, NOW));
    }

    @Test
    void shouldReserveReleaseAndShipStock() {
        Batch batch = Batch.receive(1L, "LOT-001", 100, NOW, null, NOW);

        batch.reserve(30, LATER);
        assertEquals(30, batch.quantityReserved());
        assertEquals(70, batch.availableQuantity());

        batch.release(10, LATER);
        assertEquals(20, batch.quantityReserved());

        batch.shipReserved(20, LATER);
        assertEquals(80, batch.quantityOnHand());
        assertEquals(0, batch.quantityReserved());
        assertEquals(80, batch.availableQuantity());
    }

    @Test
    void shouldRejectReservationAboveAvailableStock() {
        Batch batch = Batch.receive(1L, "LOT-001", 10, NOW, null, NOW);

        assertThrows(IllegalStateException.class, () -> batch.reserve(11, LATER));
    }

    @Test
    void shouldOnlySoftDeleteEmptyBatch() {
        Batch batch = Batch.receive(1L, "LOT-001", 10, NOW, null, NOW);

        assertThrows(IllegalStateException.class, () -> batch.softDelete(LATER));

        batch.removeAvailableStock(10, LATER);
        batch.softDelete(LATER);

        assertTrue(batch.isDeleted());
        assertEquals(LATER, batch.deletedAt());
        assertThrows(IllegalStateException.class, () -> batch.addStock(1, LATER));
    }
}

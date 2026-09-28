package be.mjodheim.cellar.ordering.internal.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private static final Instant NOW = Instant.parse("2026-09-28T12:00:00Z");
    private static final Instant LATER = Instant.parse("2026-09-28T13:00:00Z");

    @Test
    void shouldCreateDraftOrderAndAddLine() {
        Order order = Order.create("ORD-1", "CLIENT-1", NOW);

        order.addLine(1L, "Hydromel", 2, new BigDecimal("14.90"), LATER);

        assertEquals(OrderStatus.DRAFT, order.status());
        assertEquals(1, order.lines().size());
        assertEquals(new BigDecimal("29.80"), order.total());
    }

    @Test
    void shouldConfirmPrepareAndShipInOrder() {
        Order order = Order.create("ORD-1", null, NOW);
        order.addLine(1L, "Hydromel", 1, new BigDecimal("14.90"), NOW);

        order.confirm(LATER);
        assertEquals(OrderStatus.CONFIRMED, order.status());

        order.startPreparation(LATER);
        assertEquals(OrderStatus.PREPARING, order.status());

        order.ship(LATER);
        assertEquals(OrderStatus.SHIPPED, order.status());
    }

    @Test
    void shouldRejectConfirmationWithoutLine() {
        Order order = Order.create("ORD-1", null, NOW);

        assertThrows(IllegalStateException.class, () -> order.confirm(LATER));
    }

    @Test
    void shouldRejectAddingLineAfterConfirmation() {
        Order order = Order.create("ORD-1", null, NOW);
        order.addLine(1L, "Hydromel", 1, new BigDecimal("14.90"), NOW);
        order.confirm(LATER);

        assertThrows(IllegalStateException.class,
                () -> order.addLine(2L, "Bière", 1, new BigDecimal("4.90"), LATER));
    }

    @Test
    void shouldNotCancelShippedOrder() {
        Order order = Order.create("ORD-1", null, NOW);
        order.addLine(1L, "Hydromel", 1, new BigDecimal("14.90"), NOW);
        order.confirm(LATER);
        order.startPreparation(LATER);
        order.ship(LATER);

        assertThrows(IllegalStateException.class, () -> order.cancel(LATER));
    }

    @Test
    void shouldOnlySoftDeleteDraftOrCancelledOrder() {
        Order draft = Order.create("ORD-1", null, NOW);
        draft.softDelete(LATER);
        assertTrue(draft.isDeleted());

        Order confirmed = Order.create("ORD-2", null, NOW);
        confirmed.addLine(1L, "Hydromel", 1, BigDecimal.ONE, NOW);
        confirmed.confirm(LATER);

        assertThrows(IllegalStateException.class, () -> confirmed.softDelete(LATER));
    }
}

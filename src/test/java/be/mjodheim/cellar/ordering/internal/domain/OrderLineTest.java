package be.mjodheim.cellar.ordering.internal.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class OrderLineTest {

    @Test
    void shouldCalculateLineTotal() {
        OrderLine line = OrderLine.create(
                1L,
                "Hydromel",
                3,
                new BigDecimal("12.50"),
                Instant.parse("2026-09-28T12:00:00Z")
        );

        assertEquals(new BigDecimal("37.50"), line.total());
    }

    @Test
    void shouldRejectInvalidQuantity() {
        assertThrows(IllegalArgumentException.class, () -> OrderLine.create(
                1L,
                "Hydromel",
                0,
                BigDecimal.TEN,
                Instant.now()
        ));
    }
}

package be.mjodheim.cellar.ordering.internal.adapter.out.persistence;

import be.mjodheim.cellar.ordering.internal.domain.Order;
import be.mjodheim.cellar.ordering.internal.domain.OrderLine;
import be.mjodheim.cellar.ordering.internal.domain.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderPersistenceMapperTest {

    @Test
    void shouldRoundTripOrderWithoutLosingBusinessData() {
        Instant now = Instant.parse("2026-09-28T12:00:00Z");
        Order order = Order.rehydrate(
                10L,
                "ORD-10",
                "REF",
                OrderStatus.CONFIRMED,
                List.of(OrderLine.rehydrate(
                        20L,
                        1L,
                        "Hydromel",
                        2,
                        new BigDecimal("14.90"),
                        now
                )),
                now,
                now,
                null
        );

        Order restored = OrderPersistenceMapper.toDomain(OrderPersistenceMapper.toEntity(order));

        assertEquals(order.id(), restored.id());
        assertEquals(order.orderNumber(), restored.orderNumber());
        assertEquals(order.status(), restored.status());
        assertEquals(order.lines().getFirst().id(), restored.lines().getFirst().id());
        assertEquals(order.total(), restored.total());
    }
}

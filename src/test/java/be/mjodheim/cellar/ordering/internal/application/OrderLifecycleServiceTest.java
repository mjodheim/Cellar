package be.mjodheim.cellar.ordering.internal.application;

import be.mjodheim.cellar.inventory.InventoryOperations;
import be.mjodheim.cellar.ordering.internal.application.port.OrderRepository;
import be.mjodheim.cellar.ordering.internal.domain.Order;
import be.mjodheim.cellar.ordering.internal.domain.OrderLine;
import be.mjodheim.cellar.ordering.internal.domain.OrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderLifecycleServiceTest {

    @Mock OrderRepository orderRepository;
    @Mock InventoryOperations inventoryOperations;

    @Test
    void shouldAllocateStockWhenConfirming() {
        Order order = persistedDraft();
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = new OrderLifecycleService(orderRepository, inventoryOperations).confirm(10L);

        assertEquals(OrderStatus.CONFIRMED, result.status());
        verify(inventoryOperations).allocate(100L, 1L, 2);
    }

    @Test
    void shouldReleaseStockWhenCancellingConfirmedOrder() {
        Order order = persistedDraft();
        order.confirm(Instant.parse("2026-09-28T13:00:00Z"));

        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = new OrderLifecycleService(orderRepository, inventoryOperations).cancel(10L);

        assertEquals(OrderStatus.CANCELLED, result.status());
        verify(inventoryOperations).release(100L);
    }

    @Test
    void shouldConsumeStockWhenShipping() {
        Order order = persistedDraft();
        order.confirm(Instant.parse("2026-09-28T13:00:00Z"));
        order.startPreparation(Instant.parse("2026-09-28T14:00:00Z"));

        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = new OrderLifecycleService(orderRepository, inventoryOperations).ship(10L);

        assertEquals(OrderStatus.SHIPPED, result.status());
        verify(inventoryOperations).consume(100L);
    }

    private static Order persistedDraft() {
        Instant now = Instant.parse("2026-09-28T12:00:00Z");
        OrderLine line = OrderLine.rehydrate(
                100L,
                1L,
                "Hydromel",
                2,
                new BigDecimal("14.90"),
                now
        );
        return Order.rehydrate(
                10L,
                "ORD-TEST",
                null,
                OrderStatus.DRAFT,
                List.of(line),
                now,
                now,
                null
        );
    }
}

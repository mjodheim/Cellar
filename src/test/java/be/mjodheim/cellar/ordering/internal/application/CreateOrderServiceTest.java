package be.mjodheim.cellar.ordering.internal.application;

import be.mjodheim.cellar.catalog.CatalogProductView;
import be.mjodheim.cellar.catalog.CatalogProducts;
import be.mjodheim.cellar.ordering.internal.application.port.OrderRepository;
import be.mjodheim.cellar.ordering.internal.domain.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateOrderServiceTest {

    @Mock OrderRepository orderRepository;
    @Mock CatalogProducts catalogProducts;

    @Test
    void shouldUseCatalogSnapshotWhenCreatingOrder() {
        when(catalogProducts.getProduct(1L))
                .thenReturn(new CatalogProductView(1L, "Hydromel", new BigDecimal("14.90"), true));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order order = new CreateOrderService(orderRepository, catalogProducts)
                .create("CLIENT-42", List.of(new CreateOrderLineCommand(1L, 2)));

        assertEquals("Hydromel", order.lines().getFirst().productName());
        assertEquals(new BigDecimal("14.90"), order.lines().getFirst().unitPrice());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldRejectInactiveProduct() {
        when(catalogProducts.getProduct(1L))
                .thenReturn(new CatalogProductView(1L, "Ancien", BigDecimal.TEN, false));

        assertThrows(ProductUnavailableException.class, () ->
                new CreateOrderService(orderRepository, catalogProducts)
                        .create(null, List.of(new CreateOrderLineCommand(1L, 1)))
        );

        verify(orderRepository, never()).save(any());
    }
}

package be.mjodheim.cellar.ordering.internal.adapter.in.web;

import be.mjodheim.cellar.ordering.internal.application.CreateOrderService;
import be.mjodheim.cellar.ordering.internal.application.OrderLifecycleService;
import be.mjodheim.cellar.ordering.internal.application.OrderNotFoundException;
import be.mjodheim.cellar.ordering.internal.application.OrderQueryService;
import be.mjodheim.cellar.ordering.internal.domain.Order;
import be.mjodheim.cellar.ordering.internal.domain.OrderLine;
import be.mjodheim.cellar.ordering.internal.domain.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock CreateOrderService createOrderService;
    @Mock OrderQueryService queryService;
    @Mock OrderLifecycleService lifecycleService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                new OrderController(createOrderService, queryService, lifecycleService)
        ).build();
    }

    @Test
    void shouldCreateOrder() throws Exception {
        when(createOrderService.create(eq("CLIENT-1"), anyList())).thenReturn(order(OrderStatus.DRAFT));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerReference": "CLIENT-1",
                                  "lines": [
                                    { "productId": 1, "quantity": 2 }
                                  ]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/orders/10"))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.lines[0].productName").value("Hydromel"))
                .andExpect(jsonPath("$.total").value(29.80));
    }

    @Test
    void shouldConfirmOrder() throws Exception {
        when(lifecycleService.confirm(10L)).thenReturn(order(OrderStatus.CONFIRMED));

        mockMvc.perform(post("/api/orders/10/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void shouldReturn404ForUnknownOrder() throws Exception {
        when(queryService.findById(404L)).thenThrow(new OrderNotFoundException(404L));

        mockMvc.perform(get("/api/orders/404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Order not found"));
    }

    private static Order order(OrderStatus status) {
        Instant now = Instant.parse("2026-09-28T12:00:00Z");
        return Order.rehydrate(
                10L,
                "ORD-TEST",
                "CLIENT-1",
                status,
                List.of(OrderLine.rehydrate(
                        100L,
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
    }
}

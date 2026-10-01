package be.mjodheim.cellar.inventory.internal.adapter.in.web;

import be.mjodheim.cellar.inventory.internal.application.BatchLifecycleService;
import be.mjodheim.cellar.inventory.internal.application.BatchNotFoundException;
import be.mjodheim.cellar.inventory.internal.application.InventoryQueryService;
import be.mjodheim.cellar.inventory.internal.application.ReceiveBatchService;
import be.mjodheim.cellar.inventory.internal.domain.Batch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class InventoryControllerTest {

    @Mock ReceiveBatchService receiveBatchService;
    @Mock InventoryQueryService queryService;
    @Mock BatchLifecycleService lifecycleService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                new InventoryController(receiveBatchService, queryService, lifecycleService)
        ).build();
    }

    @Test
    void shouldReceiveBatch() throws Exception {
        when(receiveBatchService.receive(eq(1L), eq("LOT-001"), eq(20), any(Instant.class), isNull()))
                .thenReturn(batch(5L, "LOT-001"));

        mockMvc.perform(post("/api/inventory/batches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "productId": 1,
                                  "lotNumber": "LOT-001",
                                  "quantity": 20,
                                  "receivedAt": "2026-09-28T12:00:00Z",
                                  "expiresOn": null
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/inventory/batches/5"))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.availableQuantity").value(20));
    }

    @Test
    void shouldListProductBatches() throws Exception {
        when(queryService.findBatchesForProduct(1L)).thenReturn(List.of(batch(5L, "LOT-001")));

        mockMvc.perform(get("/api/inventory/products/1/batches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lotNumber").value("LOT-001"));
    }

    @Test
    void shouldReturn404ForUnknownBatch() throws Exception {
        when(queryService.findBatchById(404L)).thenThrow(new BatchNotFoundException(404L));

        mockMvc.perform(get("/api/inventory/batches/404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Batch not found"));
    }

    private static Batch batch(Long id, String lot) {
        Instant now = Instant.parse("2026-09-28T12:00:00Z");
        return Batch.rehydrate(id, 1L, lot, 20, 20, 0, now, null, now, now, null);
    }
}

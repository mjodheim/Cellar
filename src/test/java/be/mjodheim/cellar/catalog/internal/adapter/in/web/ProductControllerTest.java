package be.mjodheim.cellar.catalog.internal.adapter.in.web;

import be.mjodheim.cellar.catalog.internal.application.CreateProductService;
import be.mjodheim.cellar.catalog.internal.application.ProductAlreadyExistsException;
import be.mjodheim.cellar.catalog.internal.application.ProductNotFoundException;
import be.mjodheim.cellar.catalog.internal.application.ProductQueryService;
import be.mjodheim.cellar.catalog.internal.domain.Product;
import be.mjodheim.cellar.catalog.internal.domain.ProductType;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private static final Instant NOW = Instant.parse("2026-09-28T12:00:00Z");

    @Mock
    private CreateProductService createProductService;

    @Mock
    private ProductQueryService productQueryService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ProductController controller = new ProductController(
                createProductService,
                productQueryService
        );

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void shouldCreateProductAndReturn201() throws Exception {
        when(createProductService.create(
                eq("Hydromel"),
                eq(ProductType.MEAD),
                eq("Traditionnel"),
                eq(750),
                eq(new BigDecimal("14.90"))
        )).thenReturn(product(1L, "Hydromel"));

        mockMvc.perform(post("/api/catalog/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Hydromel",
                                  "type": "MEAD",
                                  "description": "Traditionnel",
                                  "volumeMl": 750,
                                  "price": 14.90
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/catalog/products/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Hydromel"))
                .andExpect(jsonPath("$.type").value("MEAD"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void shouldReturn400ForInvalidRequest() throws Exception {
        mockMvc.perform(post("/api/catalog/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "type": "MEAD",
                                  "description": "Invalide",
                                  "volumeMl": 0,
                                  "price": -1
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn409WhenProductAlreadyExists() throws Exception {
        when(createProductService.create(
                anyString(),
                any(ProductType.class),
                any(),
                anyInt(),
                any(BigDecimal.class)
        )).thenThrow(new ProductAlreadyExistsException("Hydromel"));

        mockMvc.perform(post("/api/catalog/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Hydromel",
                                  "type": "MEAD",
                                  "description": null,
                                  "volumeMl": 750,
                                  "price": 14.90
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Product already exists"));
    }

    @Test
    void shouldReturnAllProducts() throws Exception {
        when(productQueryService.findAll()).thenReturn(List.of(
                product(1L, "Hydromel"),
                product(2L, "Bière")
        ));

        mockMvc.perform(get("/api/catalog/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Hydromel"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Bière"));
    }

    @Test
    void shouldReturnProductById() throws Exception {
        when(productQueryService.findById(42L)).thenReturn(product(42L, "Hydromel"));

        mockMvc.perform(get("/api/catalog/products/42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value("Hydromel"));
    }

    @Test
    void shouldReturn404WhenProductDoesNotExist() throws Exception {
        when(productQueryService.findById(404L))
                .thenThrow(new ProductNotFoundException(404L));

        mockMvc.perform(get("/api/catalog/products/404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Product not found"));
    }

    private static Product product(Long id, String name) {
        return Product.rehydrate(
                id,
                name,
                ProductType.MEAD,
                "Traditionnel",
                750,
                new BigDecimal("14.90"),
                true,
                NOW,
                NOW
        );
    }
}

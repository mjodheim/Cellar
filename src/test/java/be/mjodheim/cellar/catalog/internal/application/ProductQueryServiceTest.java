package be.mjodheim.cellar.catalog.internal.application;

import be.mjodheim.cellar.catalog.internal.application.port.ProductRepository;
import be.mjodheim.cellar.catalog.internal.domain.Product;
import be.mjodheim.cellar.catalog.internal.domain.ProductType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductQueryServiceTest {

    private static final Instant NOW = Instant.parse("2026-09-28T12:00:00Z");

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductQueryService service;

    @Test
    void shouldReturnAllProducts() {
        Product first = product(1L, "Hydromel");
        Product second = product(2L, "Bière");

        when(productRepository.findAll()).thenReturn(List.of(first, second));

        List<Product> products = service.findAll();

        assertEquals(2, products.size());
        assertEquals(1L, products.get(0).id());
        assertEquals(2L, products.get(1).id());
    }

    @Test
    void shouldReturnProductById() {
        Product expected = product(42L, "Hydromel");

        when(productRepository.findById(42L)).thenReturn(Optional.of(expected));

        Product actual = service.findById(42L);

        assertSame(expected, actual);
    }

    @Test
    void shouldThrowWhenProductDoesNotExist() {
        when(productRepository.findById(404L)).thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> service.findById(404L)
        );

        assertTrue(exception.getMessage().contains("404"));
    }

    private static Product product(Long id, String name) {
        return Product.rehydrate(
                id,
                name,
                ProductType.MEAD,
                null,
                750,
                new BigDecimal("14.90"),
                true,
                NOW,
                NOW
        );
    }
}

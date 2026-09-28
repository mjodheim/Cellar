package be.mjodheim.cellar.catalog.internal.adapter.out.persistence;

import be.mjodheim.cellar.catalog.internal.domain.Product;
import be.mjodheim.cellar.catalog.internal.domain.ProductType;
import org.junit.jupiter.api.BeforeEach;
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
class ProductPersistenceAdapterTest {

    private static final Instant NOW = Instant.parse("2026-09-28T12:00:00Z");

    @Mock
    private JpaProductRepository jpaRepository;

    private ProductPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ProductPersistenceAdapter(jpaRepository);
    }

    @Test
    void shouldSaveProductAndReturnDomainObjectWithGeneratedId() {
        Product product = Product.create(
                "Hydromel",
                ProductType.MEAD,
                null,
                750,
                new BigDecimal("14.90"),
                NOW
        );

        when(jpaRepository.save(any(ProductEntity.class))).thenAnswer(invocation -> {
            ProductEntity entity = invocation.getArgument(0);
            return new ProductEntity(
                    1L,
                    entity.name(),
                    entity.type(),
                    entity.description(),
                    entity.volumeMl(),
                    entity.price(),
                    entity.active(),
                    entity.createdAt(),
                    entity.updatedAt()
            );
        });

        Product saved = adapter.save(product);

        assertEquals(1L, saved.id());
        assertEquals("Hydromel", saved.name());
        verify(jpaRepository).save(any(ProductEntity.class));
    }

    @Test
    void shouldFindProductById() {
        when(jpaRepository.findById(5L)).thenReturn(Optional.of(entity(5L, "Hydromel")));

        Optional<Product> result = adapter.findById(5L);

        assertTrue(result.isPresent());
        assertEquals(5L, result.orElseThrow().id());
    }

    @Test
    void shouldReturnEmptyWhenProductDoesNotExist() {
        when(jpaRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(adapter.findById(99L).isEmpty());
    }

    @Test
    void shouldReturnAllProductsInRepositoryOrder() {
        when(jpaRepository.findAllByOrderByIdAsc()).thenReturn(List.of(
                entity(1L, "Premier"),
                entity(2L, "Deuxième")
        ));

        List<Product> products = adapter.findAll();

        assertEquals(List.of(1L, 2L), products.stream().map(Product::id).toList());
    }

    @Test
    void shouldCheckNameExistenceIgnoringCase() {
        when(jpaRepository.existsByNameIgnoreCase("HYDROMEL")).thenReturn(true);

        assertTrue(adapter.existsByNameIgnoreCase("HYDROMEL"));
        verify(jpaRepository).existsByNameIgnoreCase("HYDROMEL");
    }

    private static ProductEntity entity(Long id, String name) {
        return new ProductEntity(
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

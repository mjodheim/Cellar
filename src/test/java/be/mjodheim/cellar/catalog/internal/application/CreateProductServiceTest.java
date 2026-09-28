package be.mjodheim.cellar.catalog.internal.application;

import be.mjodheim.cellar.catalog.internal.application.port.ProductRepository;
import be.mjodheim.cellar.catalog.internal.domain.Product;
import be.mjodheim.cellar.catalog.internal.domain.ProductType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CreateProductService service;

    @Test
    void shouldCreateAndSaveProduct() {
        when(productRepository.existsByNameIgnoreCase("Hydromel Classique")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            return Product.rehydrate(
                    1L,
                    product.name(),
                    product.type(),
                    product.description(),
                    product.volumeMl(),
                    product.price(),
                    product.active(),
                    product.createdAt(),
                    product.updatedAt()
            );
        });

        Product created = service.create(
                "Hydromel Classique",
                ProductType.MEAD,
                "Hydromel traditionnel",
                750,
                new BigDecimal("14.90")
        );

        assertEquals(1L, created.id());
        assertEquals("Hydromel Classique", created.name());
        assertEquals(ProductType.MEAD, created.type());
        assertTrue(created.active());

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());

        Product productBeforePersistence = captor.getValue();
        assertNull(productBeforePersistence.id());
        assertEquals("Hydromel Classique", productBeforePersistence.name());
        assertNotNull(productBeforePersistence.createdAt());
        assertEquals(productBeforePersistence.createdAt(), productBeforePersistence.updatedAt());
    }

    @Test
    void shouldRejectDuplicateNameIgnoringCase() {
        when(productRepository.existsByNameIgnoreCase("Hydromel Classique")).thenReturn(true);

        ProductAlreadyExistsException exception = assertThrows(
                ProductAlreadyExistsException.class,
                () -> service.create(
                        "Hydromel Classique",
                        ProductType.MEAD,
                        null,
                        750,
                        new BigDecimal("14.90")
                )
        );

        assertTrue(exception.getMessage().contains("Hydromel Classique"));
        verify(productRepository, never()).save(any());
    }
}

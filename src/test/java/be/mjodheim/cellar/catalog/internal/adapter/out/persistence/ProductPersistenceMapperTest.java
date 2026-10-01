package be.mjodheim.cellar.catalog.internal.adapter.out.persistence;

import be.mjodheim.cellar.catalog.internal.domain.Product;
import be.mjodheim.cellar.catalog.internal.domain.ProductType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ProductPersistenceMapperTest {

    private static final Instant CREATED_AT = Instant.parse("2026-01-01T10:00:00Z");
    private static final Instant UPDATED_AT = Instant.parse("2026-06-01T11:00:00Z");

    @Test
    void shouldMapDomainToEntityWithoutLosingData() {
        Product product = Product.rehydrate(
                42L,
                "Hydromel Réserve",
                ProductType.MEAD,
                "Vieilli en fût",
                750,
                new BigDecimal("24.90"),
                false,
                CREATED_AT,
                UPDATED_AT
        );

        ProductEntity entity = ProductPersistenceMapper.toEntity(product);

        assertEquals(42L, entity.id());
        assertEquals("Hydromel Réserve", entity.name());
        assertEquals(ProductType.MEAD, entity.type());
        assertEquals("Vieilli en fût", entity.description());
        assertEquals(750, entity.volumeMl());
        assertEquals(new BigDecimal("24.90"), entity.price());
        assertFalse(entity.active());
        assertEquals(CREATED_AT, entity.createdAt());
        assertEquals(UPDATED_AT, entity.updatedAt());
    }

    @Test
    void shouldMapEntityToDomainWithoutLosingData() {
        ProductEntity entity = new ProductEntity(
                7L,
                "Bière Mjödheim",
                ProductType.BEER,
                "Brassée localement",
                330,
                new BigDecimal("4.50"),
                true,
                CREATED_AT,
                UPDATED_AT
        );

        Product product = ProductPersistenceMapper.toDomain(entity);

        assertEquals(7L, product.id());
        assertEquals("Bière Mjödheim", product.name());
        assertEquals(ProductType.BEER, product.type());
        assertEquals("Brassée localement", product.description());
        assertEquals(330, product.volumeMl());
        assertEquals(new BigDecimal("4.50"), product.price());
        assertTrue(product.active());
        assertEquals(CREATED_AT, product.createdAt());
        assertEquals(UPDATED_AT, product.updatedAt());
    }
}

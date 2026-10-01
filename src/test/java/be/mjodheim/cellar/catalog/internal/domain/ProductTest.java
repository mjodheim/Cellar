package be.mjodheim.cellar.catalog.internal.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    private static final Instant NOW = Instant.parse("2026-09-28T12:00:00Z");
    private static final Instant LATER = Instant.parse("2026-09-28T13:00:00Z");

    @Test
    void shouldCreateAValidProduct() {
        Product product = Product.create(
                "Hydromel classique",
                ProductType.MEAD,
                "Hydromel traditionnel",
                750,
                new BigDecimal("14.90"),
                NOW
        );

        assertNull(product.id());
        assertEquals("Hydromel classique", product.name());
        assertEquals(ProductType.MEAD, product.type());
        assertEquals("Hydromel traditionnel", product.description());
        assertEquals(750, product.volumeMl());
        assertEquals(new BigDecimal("14.90"), product.price());
        assertTrue(product.active());
        assertEquals(NOW, product.createdAt());
        assertEquals(NOW, product.updatedAt());
    }

    @Test
    void shouldTrimProductNameOnCreation() {
        Product product = Product.create(
                "  Hydromel classique  ",
                ProductType.MEAD,
                null,
                750,
                new BigDecimal("14.90"),
                NOW
        );

        assertEquals("Hydromel classique", product.name());
    }

    @Test
    void shouldRejectNullName() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Product.create(
                        null,
                        ProductType.MEAD,
                        null,
                        750,
                        new BigDecimal("14.90"),
                        NOW
                )
        );

        assertEquals("Product name is required", exception.getMessage());
    }

    @Test
    void shouldRejectBlankName() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Product.create(
                        "   ",
                        ProductType.MEAD,
                        null,
                        750,
                        new BigDecimal("14.90"),
                        NOW
                )
        );

        assertEquals("Product name is required", exception.getMessage());
    }

    @Test
    void shouldRejectNullProductType() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Product.create(
                        "Hydromel classique",
                        null,
                        null,
                        750,
                        new BigDecimal("14.90"),
                        NOW
                )
        );

        assertEquals("Product type is required", exception.getMessage());
    }

    @Test
    void shouldRejectZeroVolume() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Product.create(
                        "Hydromel classique",
                        ProductType.MEAD,
                        null,
                        0,
                        new BigDecimal("14.90"),
                        NOW
                )
        );

        assertEquals("Product volume must be greater than zero", exception.getMessage());
    }

    @Test
    void shouldRejectNegativeVolume() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Product.create(
                        "Hydromel classique",
                        ProductType.MEAD,
                        null,
                        -1,
                        new BigDecimal("14.90"),
                        NOW
                )
        );
    }

    @Test
    void shouldRejectNullPrice() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Product.create(
                        "Hydromel classique",
                        ProductType.MEAD,
                        null,
                        750,
                        null,
                        NOW
                )
        );

        assertEquals("Product price cannot be negative", exception.getMessage());
    }

    @Test
    void shouldRejectNegativePrice() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Product.create(
                        "Hydromel classique",
                        ProductType.MEAD,
                        null,
                        750,
                        new BigDecimal("-0.01"),
                        NOW
                )
        );

        assertEquals("Product price cannot be negative", exception.getMessage());
    }

    @Test
    void shouldAllowZeroPrice() {
        Product product = Product.create(
                "Produit gratuit",
                ProductType.BEER,
                null,
                330,
                BigDecimal.ZERO,
                NOW
        );

        assertEquals(BigDecimal.ZERO, product.price());
    }

    @Test
    void shouldRejectNullCreationInstant() {
        assertThrows(
                NullPointerException.class,
                () -> Product.create(
                        "Hydromel classique",
                        ProductType.MEAD,
                        null,
                        750,
                        new BigDecimal("14.90"),
                        null
                )
        );
    }

    @Test
    void shouldRehydrateAnExistingProduct() {
        Instant createdAt = Instant.parse("2026-01-01T10:00:00Z");
        Instant updatedAt = Instant.parse("2026-06-01T10:00:00Z");

        Product product = Product.rehydrate(
                42L,
                "Hydromel ancien",
                ProductType.MEAD,
                "Produit déjà enregistré",
                750,
                new BigDecimal("18.50"),
                false,
                createdAt,
                updatedAt
        );

        assertEquals(42L, product.id());
        assertEquals("Hydromel ancien", product.name());
        assertEquals(ProductType.MEAD, product.type());
        assertEquals("Produit déjà enregistré", product.description());
        assertEquals(750, product.volumeMl());
        assertEquals(new BigDecimal("18.50"), product.price());
        assertFalse(product.active());
        assertEquals(createdAt, product.createdAt());
        assertEquals(updatedAt, product.updatedAt());
    }

    @Test
    void shouldChangeProductDetailsAndUpdateTimestamp() {
        Product product = Product.create(
                "Ancien nom",
                ProductType.MEAD,
                "Ancienne description",
                750,
                new BigDecimal("14.90"),
                NOW
        );

        product.changeDetails(
                "Nouveau nom",
                ProductType.BEER,
                "Nouvelle description",
                330,
                new BigDecimal("4.90"),
                LATER
        );

        assertEquals("Nouveau nom", product.name());
        assertEquals(ProductType.BEER, product.type());
        assertEquals("Nouvelle description", product.description());
        assertEquals(330, product.volumeMl());
        assertEquals(new BigDecimal("4.90"), product.price());
        assertEquals(NOW, product.createdAt());
        assertEquals(LATER, product.updatedAt());
    }

    @Test
    void shouldApplyValidationWhenChangingDetails() {
        Product product = Product.create(
                "Hydromel classique",
                ProductType.MEAD,
                null,
                750,
                new BigDecimal("14.90"),
                NOW
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> product.changeDetails(
                        "",
                        ProductType.MEAD,
                        null,
                        750,
                        new BigDecimal("14.90"),
                        LATER
                )
        );
    }

    @Test
    void shouldDeactivateProductAndUpdateTimestamp() {
        Product product = Product.create(
                "Hydromel classique",
                ProductType.MEAD,
                null,
                750,
                new BigDecimal("14.90"),
                NOW
        );

        product.deactivate(LATER);

        assertFalse(product.active());
        assertEquals(LATER, product.updatedAt());
    }

    @Test
    void shouldKeepTimestampWhenDeactivatingAnAlreadyInactiveProduct() {
        Product product = Product.rehydrate(
                42L,
                "Hydromel classique",
                ProductType.MEAD,
                null,
                750,
                new BigDecimal("14.90"),
                false,
                NOW,
                NOW
        );

        product.deactivate(LATER);

        assertFalse(product.active());
        assertEquals(NOW, product.updatedAt());
    }
}

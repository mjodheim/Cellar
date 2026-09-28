package be.mjodheim.cellar.catalog.internal.adapter.in.web;

import be.mjodheim.cellar.catalog.internal.domain.Product;
import be.mjodheim.cellar.catalog.internal.domain.ProductType;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * HTTP representation of a catalogue product.
 *
 * @param id product identifier
 * @param name product name
 * @param type product family
 * @param description optional description
 * @param volumeMl container volume in millilitres
 * @param price current unit price
 * @param active whether the product is currently active
 * @param createdAt creation timestamp
 * @param updatedAt last modification timestamp
 */
record ProductResponse(
        Long id,
        String name,
        ProductType type,
        String description,
        int volumeMl,
        BigDecimal price,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {

    /**
     * Maps a domain product to its HTTP representation.
     *
     * @param product domain product
     * @return immutable response projection
     */
    static ProductResponse from(Product product) {
        return new ProductResponse(
                product.id(),
                product.name(),
                product.type(),
                product.description(),
                product.volumeMl(),
                product.price(),
                product.active(),
                product.createdAt(),
                product.updatedAt()
        );
    }
}

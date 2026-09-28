package be.mjodheim.cellar.catalog.internal.adapter.in.web;

import be.mjodheim.cellar.catalog.internal.domain.Product;
import be.mjodheim.cellar.catalog.internal.domain.ProductType;

import java.math.BigDecimal;
import java.time.Instant;

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

package be.mjodheim.cellar.catalog;

import java.math.BigDecimal;

public record CatalogProductView(
        Long id,
        String name,
        BigDecimal price,
        boolean active
) {}

package be.mjodheim.cellar.catalog;

import java.math.BigDecimal;

/**
 * Stable public projection of a catalogue product exposed to other modules.
 *
 * @param id product identifier
 * @param name product display name
 * @param price current catalogue unit price
 * @param active whether the product can currently be ordered
 */
public record CatalogProductView(
        Long id,
        String name,
        BigDecimal price,
        boolean active
) {}

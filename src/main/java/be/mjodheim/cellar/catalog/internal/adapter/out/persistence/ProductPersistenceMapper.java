package be.mjodheim.cellar.catalog.internal.adapter.out.persistence;

import be.mjodheim.cellar.catalog.internal.domain.Product;

/**
 * Bidirectional mapper between the Catalog domain model and its JPA representation.
 */
final class ProductPersistenceMapper {

    private ProductPersistenceMapper() {
    }

    /**
     * Converts a domain product to a persistence entity.
     *
     * @param product domain product
     * @return persistence representation
     */
    static ProductEntity toEntity(Product product) {
        return new ProductEntity(
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

    /**
     * Rehydrates a domain product from persisted data.
     *
     * @param entity persistence entity
     * @return domain product
     */
    static Product toDomain(ProductEntity entity) {
        return Product.rehydrate(
                entity.id(),
                entity.name(),
                entity.type(),
                entity.description(),
                entity.volumeMl(),
                entity.price(),
                entity.active(),
                entity.createdAt(),
                entity.updatedAt()
        );
    }
}

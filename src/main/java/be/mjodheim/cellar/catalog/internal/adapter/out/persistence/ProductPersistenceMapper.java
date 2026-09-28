package be.mjodheim.cellar.catalog.internal.adapter.out.persistence;

import be.mjodheim.cellar.catalog.internal.domain.Product;

final class ProductPersistenceMapper {

    private ProductPersistenceMapper() {
    }

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

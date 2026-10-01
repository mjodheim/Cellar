package be.mjodheim.cellar.catalog.internal.adapter.out.persistence;

import be.mjodheim.cellar.catalog.internal.application.port.ProductRepository;
import be.mjodheim.cellar.catalog.internal.domain.Product;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Persistence adapter implementing the Catalog application's product repository port.
 *
 * <p>All conversion between JPA entities and domain objects is delegated to
 * {@link ProductPersistenceMapper}.</p>
 */
@Repository
class ProductPersistenceAdapter implements ProductRepository {

    private final JpaProductRepository repository;

    ProductPersistenceAdapter(JpaProductRepository repository) {
        this.repository = repository;
    }

    /** {@inheritDoc} */
    @Override
    public Product save(Product product) {
        ProductEntity entity = ProductPersistenceMapper.toEntity(product);
        ProductEntity saved = repository.save(entity);
        return ProductPersistenceMapper.toDomain(saved);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Product> findById(Long id) {
        return repository.findById(id)
                .map(ProductPersistenceMapper::toDomain);
    }

    /** {@inheritDoc} */
    @Override
    public List<Product> findAll() {
        return repository.findAllByOrderByIdAsc().stream()
                .map(ProductPersistenceMapper::toDomain)
                .toList();
    }

    /** {@inheritDoc} */
    @Override
    public boolean existsByNameIgnoreCase(String name) {
        return repository.existsByNameIgnoreCase(name);
    }
}

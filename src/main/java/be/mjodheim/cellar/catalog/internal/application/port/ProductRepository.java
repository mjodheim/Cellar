package be.mjodheim.cellar.catalog.internal.application.port;

import be.mjodheim.cellar.catalog.internal.domain.Product;

import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(Long id);

    boolean existsByNameIgnoreCase(String name);
}

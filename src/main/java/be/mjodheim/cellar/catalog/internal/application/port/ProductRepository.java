package be.mjodheim.cellar.catalog.internal.application.port;

import be.mjodheim.cellar.catalog.internal.domain.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(Long id);

    List<Product> findAll();

    boolean existsByNameIgnoreCase(String name);
}

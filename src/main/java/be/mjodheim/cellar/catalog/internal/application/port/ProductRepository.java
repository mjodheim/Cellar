package be.mjodheim.cellar.catalog.internal.application.port;

import be.mjodheim.cellar.catalog.internal.domain.Product;

import java.util.List;
import java.util.Optional;

/**
 * Outbound persistence port used by Catalog application services.
 */
public interface ProductRepository {

    /**
     * Persists a product.
     *
     * @param product product to save
     * @return persisted product
     */
    Product save(Product product);

    /**
     * Finds a product by identifier.
     *
     * @param id product identifier
     * @return matching product or an empty optional
     */
    Optional<Product> findById(Long id);

    /**
     * Lists all products.
     *
     * @return products in stable repository order
     */
    List<Product> findAll();

    /**
     * Checks whether a product name already exists, ignoring letter case.
     *
     * @param name product name
     * @return {@code true} when a matching name exists
     */
    boolean existsByNameIgnoreCase(String name);
}

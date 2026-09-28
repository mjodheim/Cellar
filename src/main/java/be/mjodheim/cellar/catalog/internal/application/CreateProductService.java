package be.mjodheim.cellar.catalog.internal.application;

import be.mjodheim.cellar.catalog.internal.application.port.ProductRepository;
import be.mjodheim.cellar.catalog.internal.domain.Product;
import be.mjodheim.cellar.catalog.internal.domain.ProductType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Creates catalogue products and enforces application-level uniqueness rules.
 */
@Service
public class CreateProductService {

    private final ProductRepository productRepository;

    /**
     * Creates the service with the Catalog persistence boundary.
     *
     * @param productRepository repository port used to persist products
     */
    public CreateProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Creates and persists a new active catalogue product.
     *
     * @param name product name
     * @param type product family
     * @param description optional description
     * @param volumeMl container volume in millilitres
     * @param price unit price
     * @return persisted product
     * @throws ProductAlreadyExistsException when the requested name already exists
     * @throws IllegalArgumentException when a domain invariant is violated
     */
    @Transactional
    public Product create(
            String name,
            ProductType type,
            String description,
            int volumeMl,
            BigDecimal price
    ) {
        if (productRepository.existsByNameIgnoreCase(name)) {
            throw new ProductAlreadyExistsException(name);
        }

        Product product = Product.create(
                name,
                type,
                description,
                volumeMl,
                price,
                Instant.now()
        );

        return productRepository.save(product);
    }
}

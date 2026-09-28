package be.mjodheim.cellar.catalog.internal.application;

import be.mjodheim.cellar.catalog.internal.application.port.ProductRepository;
import be.mjodheim.cellar.catalog.internal.domain.Product;
import be.mjodheim.cellar.catalog.internal.domain.ProductType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Service
/**
 * Creates catalogue products and enforces application-level uniqueness rules.
 */
public class CreateProductService {

    private final ProductRepository productRepository;

    public CreateProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

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

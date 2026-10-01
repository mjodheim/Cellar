package be.mjodheim.cellar.catalog.internal.application;

import be.mjodheim.cellar.catalog.ProductNotFoundException;
import be.mjodheim.cellar.catalog.internal.application.port.ProductRepository;
import be.mjodheim.cellar.catalog.internal.domain.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Read-only catalogue use cases for listing and retrieving products.
 */
@Service
public class ProductQueryService {

    private final ProductRepository productRepository;

    /**
     * Creates the query service.
     *
     * @param productRepository repository port used for catalogue reads
     */
    public ProductQueryService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Returns all catalogue products.
     *
     * @return products in repository-defined order
     */
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    /**
     * Returns one catalogue product by identifier.
     *
     * @param id product identifier
     * @return matching product
     * @throws ProductNotFoundException when no product exists for the identifier
     */
    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}

package be.mjodheim.cellar.catalog.internal.application;

import be.mjodheim.cellar.catalog.CatalogProductView;
import be.mjodheim.cellar.catalog.CatalogProducts;
import be.mjodheim.cellar.catalog.internal.application.port.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Internal implementation of the public Catalog module API.
 *
 * <p>The service translates the internal Product aggregate into the stable
 * projection exposed to other modules.</p>
 */
@Service
class CatalogProductsService implements CatalogProducts {

    private final ProductRepository productRepository;

    CatalogProductsService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public CatalogProductView getProduct(Long id) {
        var product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        return new CatalogProductView(product.id(), product.name(), product.price(), product.active());
    }
}

package be.mjodheim.cellar.catalog;

/**
 * Public Catalog module API exposed to other application modules.
 *
 * <p>Consumers receive a stable projection instead of accessing Catalog's
 * internal domain or persistence classes.</p>
 */
public interface CatalogProducts {
    CatalogProductView getProduct(Long id);
}

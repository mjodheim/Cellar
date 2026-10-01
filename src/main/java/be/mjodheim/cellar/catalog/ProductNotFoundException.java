package be.mjodheim.cellar.catalog;

/**
 * Signals that a requested catalogue product does not exist.
 */
public class ProductNotFoundException extends RuntimeException {

    /**
     * Creates the exception for the missing identifier.
     *
     * @param id missing product identifier
     */
    public ProductNotFoundException(Long id) {
        super("Product with id " + id + " was not found");
    }
}

package be.mjodheim.cellar.catalog.internal.application;

/**
 * Signals that product creation would violate the catalogue name uniqueness rule.
 */
public class ProductAlreadyExistsException extends RuntimeException {

    /**
     * Creates the exception for the conflicting product name.
     *
     * @param name duplicated product name
     */
    public ProductAlreadyExistsException(String name) {
        super("A product named '" + name + "' already exists");
    }
}

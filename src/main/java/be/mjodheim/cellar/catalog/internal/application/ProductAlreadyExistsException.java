package be.mjodheim.cellar.catalog.internal.application;

public class ProductAlreadyExistsException extends RuntimeException {

    public ProductAlreadyExistsException(String name) {
        super("A product named '" + name + "' already exists");
    }
}

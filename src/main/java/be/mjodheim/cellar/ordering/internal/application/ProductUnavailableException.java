package be.mjodheim.cellar.ordering.internal.application;

public class ProductUnavailableException extends RuntimeException {
    public ProductUnavailableException(Long productId) {
        super("Product " + productId + " is not active and cannot be ordered");
    }
}

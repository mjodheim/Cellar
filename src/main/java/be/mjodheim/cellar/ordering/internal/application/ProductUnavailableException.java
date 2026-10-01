package be.mjodheim.cellar.ordering.internal.application;

/**
 * Signals that an inactive catalogue product cannot be added to a new order.
 */
public class ProductUnavailableException extends RuntimeException {

    /**
     * Creates the exception for the unavailable product.
     *
     * @param productId product identifier
     */
    public ProductUnavailableException(Long productId) {
        super("Product " + productId + " is not active and cannot be ordered");
    }
}

package be.mjodheim.cellar.inventory.internal.application;

/**
 * Signals that a stock allocation request exceeds the currently available quantity.
 */
public class InsufficientStockException extends RuntimeException {

    /**
     * Creates an exception describing the requested and available quantities.
     *
     * @param productId product identifier
     * @param requested requested quantity
     * @param available currently available quantity
     */
    public InsufficientStockException(Long productId, int requested, int available) {
        super("Insufficient stock for product " + productId + ": requested " + requested + ", available " + available);
    }
}

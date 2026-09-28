package be.mjodheim.cellar.inventory.internal.application;

/**
 * Signals that an active lot number already exists for the same product.
 */
public class BatchAlreadyExistsException extends RuntimeException {

    /**
     * Creates the exception for the conflicting product and lot.
     *
     * @param productId product identifier
     * @param lotNumber duplicated lot reference
     */
    public BatchAlreadyExistsException(Long productId, String lotNumber) {
        super("Batch '" + lotNumber + "' already exists for product " + productId);
    }
}

package be.mjodheim.cellar.inventory.internal.application;

public class BatchAlreadyExistsException extends RuntimeException {
    public BatchAlreadyExistsException(Long productId, String lotNumber) {
        super("Batch '" + lotNumber + "' already exists for product " + productId);
    }
}

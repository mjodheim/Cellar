package be.mjodheim.cellar.inventory.internal.application;

public class BatchNotFoundException extends RuntimeException {
    public BatchNotFoundException(Long id) {
        super("Batch with id " + id + " was not found");
    }
}

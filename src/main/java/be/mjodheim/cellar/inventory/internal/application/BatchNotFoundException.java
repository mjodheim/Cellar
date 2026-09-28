package be.mjodheim.cellar.inventory.internal.application;

/**
 * Signals that a requested inventory batch cannot be found.
 */
public class BatchNotFoundException extends RuntimeException {

    /**
     * Creates the exception for the missing batch identifier.
     *
     * @param id missing batch identifier
     */
    public BatchNotFoundException(Long id) {
        super("Batch with id " + id + " was not found");
    }
}

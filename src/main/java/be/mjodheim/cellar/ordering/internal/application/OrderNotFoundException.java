package be.mjodheim.cellar.ordering.internal.application;

/**
 * Signals that a requested order cannot be found.
 */
public class OrderNotFoundException extends RuntimeException {

    /**
     * Creates the exception for the missing order identifier.
     *
     * @param id missing order identifier
     */
    public OrderNotFoundException(Long id) {
        super("Order with id " + id + " was not found");
    }
}

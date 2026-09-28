package be.mjodheim.cellar.ordering.internal.application;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long id) {
        super("Order with id " + id + " was not found");
    }
}

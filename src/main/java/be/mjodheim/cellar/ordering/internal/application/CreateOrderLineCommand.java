package be.mjodheim.cellar.ordering.internal.application;

/**
 * Application command describing one requested line during order creation.
 *
 * @param productId catalogue product identifier
 * @param quantity requested quantity
 */
public record CreateOrderLineCommand(Long productId, int quantity) {}

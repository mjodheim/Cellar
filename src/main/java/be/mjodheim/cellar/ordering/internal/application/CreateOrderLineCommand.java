package be.mjodheim.cellar.ordering.internal.application;

public record CreateOrderLineCommand(Long productId, int quantity) {}

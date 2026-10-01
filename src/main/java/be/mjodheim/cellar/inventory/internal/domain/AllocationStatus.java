package be.mjodheim.cellar.inventory.internal.domain;

/**
 * Lifecycle states of an allocation between an order line and a stock batch.
 */
public enum AllocationStatus {
    /** Stock is currently reserved for the order line. */
    RESERVED,

    /** The reservation was cancelled and stock became available again. */
    RELEASED,

    /** The reservation was consumed by shipment. */
    CONSUMED
}

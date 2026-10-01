package be.mjodheim.cellar.inventory.internal.domain;

/**
 * Categories of physical stock changes recorded by the inventory ledger.
 */
public enum StockMovementType {
    /** Stock received into inventory. */
    RECEIPT,
    /** Stock sent out for an order. */
    SHIPMENT,
    /** Positive stock adjustment. */
    ADJUSTMENT_IN,
    /** Negative stock adjustment. */
    ADJUSTMENT_OUT,
    /** Stock removed from usable inventory. */
    WASTE,
    /** Stock returned to inventory. */
    RETURN
}

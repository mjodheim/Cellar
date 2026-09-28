package be.mjodheim.cellar.inventory;

/**
 * Public Inventory module API used by Ordering to reserve, release and consume stock.
 *
 * <p>This interface is the explicit Modulith boundary between Ordering and Inventory.</p>
 */
public interface InventoryOperations {

    /**
     * Reserves stock for an order line using Inventory's FEFO rules.
     *
     * @param orderLineId order-line identifier
     * @param productId product identifier
     * @param quantity quantity to reserve
     */
    void allocate(Long orderLineId, Long productId, int quantity);

    /**
     * Releases active allocations for an order line.
     *
     * @param orderLineId order-line identifier
     */
    void release(Long orderLineId);

    /**
     * Consumes active allocations for an order line during shipment.
     *
     * @param orderLineId order-line identifier
     */
    void consume(Long orderLineId);
}

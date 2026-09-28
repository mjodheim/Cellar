package be.mjodheim.cellar.inventory;

/**
 * Public Inventory module API used by Ordering to reserve, release and consume stock.
 *
 * <p>This interface is the explicit Modulith boundary between Ordering and Inventory.</p>
 */
public interface InventoryOperations {
    void allocate(Long orderLineId, Long productId, int quantity);
    void release(Long orderLineId);
    void consume(Long orderLineId);
}

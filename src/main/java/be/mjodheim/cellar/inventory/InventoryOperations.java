package be.mjodheim.cellar.inventory;

public interface InventoryOperations {
    void allocate(Long orderLineId, Long productId, int quantity);
    void release(Long orderLineId);
    void consume(Long orderLineId);
}

package be.mjodheim.cellar.inventory.internal.application.port;

import be.mjodheim.cellar.inventory.internal.domain.Allocation;

import java.util.List;

/**
 * Outbound persistence port for stock allocations.
 */
public interface AllocationRepository {

    /**
     * Persists an allocation.
     *
     * @param allocation allocation to save
     * @return persisted allocation
     */
    Allocation save(Allocation allocation);

    /**
     * Lists allocations belonging to an order line.
     *
     * @param orderLineId order-line identifier
     * @return allocations in stable repository order
     */
    List<Allocation> findByOrderLineId(Long orderLineId);
}

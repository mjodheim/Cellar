package be.mjodheim.cellar.inventory.internal.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data repository for persisted stock allocations.
 */
interface JpaAllocationRepository extends JpaRepository<AllocationEntity, Long> {

    /**
     * Lists allocations for an order line in stable identifier order.
     *
     * @param orderLineId order-line identifier
     * @return allocation entities
     */
    List<AllocationEntity> findByOrderLineIdOrderByIdAsc(Long orderLineId);
}

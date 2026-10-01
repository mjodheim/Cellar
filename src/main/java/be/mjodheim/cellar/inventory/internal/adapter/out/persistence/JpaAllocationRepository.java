package be.mjodheim.cellar.inventory.internal.adapter.out.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select a from AllocationEntity a
            where a.orderLineId = :orderLineId
            order by a.batchId asc, a.id asc
            """)
    List<AllocationEntity> findByOrderLineIdForUpdate(@Param("orderLineId") Long orderLineId);
}

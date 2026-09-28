package be.mjodheim.cellar.inventory.internal.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

interface JpaAllocationRepository extends JpaRepository<AllocationEntity, Long> {
    List<AllocationEntity> findByOrderLineIdOrderByIdAsc(Long orderLineId);
}

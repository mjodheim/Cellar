package be.mjodheim.cellar.inventory.internal.adapter.out.persistence;

import be.mjodheim.cellar.inventory.internal.application.port.AllocationRepository;
import be.mjodheim.cellar.inventory.internal.domain.Allocation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
class AllocationPersistenceAdapter implements AllocationRepository {

    private final JpaAllocationRepository repository;

    AllocationPersistenceAdapter(JpaAllocationRepository repository) { this.repository = repository; }

    @Override
    public Allocation save(Allocation allocation) {
        return InventoryPersistenceMapper.toDomain(repository.save(InventoryPersistenceMapper.toEntity(allocation)));
    }

    @Override
    public List<Allocation> findByOrderLineId(Long orderLineId) {
        return repository.findByOrderLineIdOrderByIdAsc(orderLineId).stream()
                .map(InventoryPersistenceMapper::toDomain).toList();
    }
}

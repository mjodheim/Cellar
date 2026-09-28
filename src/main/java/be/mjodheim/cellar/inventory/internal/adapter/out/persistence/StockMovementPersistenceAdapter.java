package be.mjodheim.cellar.inventory.internal.adapter.out.persistence;

import be.mjodheim.cellar.inventory.internal.application.port.StockMovementRepository;
import be.mjodheim.cellar.inventory.internal.domain.StockMovement;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
class StockMovementPersistenceAdapter implements StockMovementRepository {

    private final JpaStockMovementRepository repository;

    StockMovementPersistenceAdapter(JpaStockMovementRepository repository) { this.repository = repository; }

    @Override
    public StockMovement save(StockMovement movement) {
        return InventoryPersistenceMapper.toDomain(repository.save(InventoryPersistenceMapper.toEntity(movement)));
    }

    @Override
    public List<StockMovement> findByBatchId(Long batchId) {
        return repository.findByBatchIdOrderByOccurredAtAscIdAsc(batchId).stream()
                .map(InventoryPersistenceMapper::toDomain).toList();
    }
}

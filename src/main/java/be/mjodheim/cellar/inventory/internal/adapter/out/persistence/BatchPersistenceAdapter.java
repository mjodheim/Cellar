package be.mjodheim.cellar.inventory.internal.adapter.out.persistence;

import be.mjodheim.cellar.inventory.internal.application.port.BatchRepository;
import be.mjodheim.cellar.inventory.internal.domain.Batch;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class BatchPersistenceAdapter implements BatchRepository {

    private final JpaBatchRepository repository;

    BatchPersistenceAdapter(JpaBatchRepository repository) { this.repository = repository; }

    @Override
    public Batch save(Batch batch) {
        return InventoryPersistenceMapper.toDomain(repository.save(InventoryPersistenceMapper.toEntity(batch)));
    }

    @Override
    public Optional<Batch> findById(Long id) {
        return repository.findByIdAndDeletedAtIsNull(id).map(InventoryPersistenceMapper::toDomain);
    }

    @Override
    public List<Batch> findByProductIdFefo(Long productId) {
        return repository.findAvailableByProductIdFefo(productId).stream()
                .map(InventoryPersistenceMapper::toDomain).toList();
    }

    @Override
    public boolean existsByProductIdAndLotNumberIgnoreCase(Long productId, String lotNumber) {
        return repository.existsByProductIdAndLotNumberIgnoreCaseAndDeletedAtIsNull(productId, lotNumber);
    }
}

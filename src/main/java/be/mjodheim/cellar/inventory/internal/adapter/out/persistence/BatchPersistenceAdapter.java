package be.mjodheim.cellar.inventory.internal.adapter.out.persistence;

import be.mjodheim.cellar.inventory.internal.application.port.BatchRepository;
import be.mjodheim.cellar.inventory.internal.domain.Batch;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Persistence adapter implementing the Inventory batch repository port.
 */
@Repository
class BatchPersistenceAdapter implements BatchRepository {

    private final JpaBatchRepository repository;

    BatchPersistenceAdapter(JpaBatchRepository repository) {
        this.repository = repository;
    }

    /** {@inheritDoc} */
    @Override
    public Batch save(Batch batch) {
        return InventoryPersistenceMapper.toDomain(repository.save(InventoryPersistenceMapper.toEntity(batch)));
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Batch> findById(Long id) {
        return repository.findByIdAndDeletedAtIsNull(id).map(InventoryPersistenceMapper::toDomain);
    }

    /** {@inheritDoc} */
    @Override
    public List<Batch> findByProductIdFefo(Long productId) {
        return repository.findAvailableByProductIdFefo(productId).stream()
                .map(InventoryPersistenceMapper::toDomain).toList();
    }

    /** {@inheritDoc} */
    @Override
    public boolean existsByProductIdAndLotNumberIgnoreCase(Long productId, String lotNumber) {
        return repository.existsByProductIdAndLotNumberIgnoreCaseAndDeletedAtIsNull(productId, lotNumber);
    }

    @Override
    public Optional<Batch> findByIdForUpdate(Long id) {
        return repository.findByIdForUpdate(id).map(InventoryPersistenceMapper::toDomain);
    }

    @Override
    public List<Batch> findByProductIdFefoForUpdate(Long productId) {
        return repository.findAvailableByProductIdForUpdate(productId).stream()
                .map(InventoryPersistenceMapper::toDomain)
                .sorted(Comparator.comparing(Batch::expiresOn,
                                Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Batch::receivedAt)
                        .thenComparing(Batch::id))
                .toList();
    }
}

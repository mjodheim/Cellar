package be.mjodheim.cellar.inventory.internal.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data repository for stock-movement ledger entries.
 */
interface JpaStockMovementRepository extends JpaRepository<StockMovementEntity, Long> {

    /**
     * Lists movements for a batch in chronological order with identifier as tie-breaker.
     *
     * @param batchId batch identifier
     * @return ordered movement entities
     */
    List<StockMovementEntity> findByBatchIdOrderByOccurredAtAscIdAsc(Long batchId);
}

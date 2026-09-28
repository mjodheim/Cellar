package be.mjodheim.cellar.inventory.internal.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

interface JpaStockMovementRepository extends JpaRepository<StockMovementEntity, Long> {
    List<StockMovementEntity> findByBatchIdOrderByOccurredAtAscIdAsc(Long batchId);
}

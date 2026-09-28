package be.mjodheim.cellar.inventory.internal.application.port;

import be.mjodheim.cellar.inventory.internal.domain.StockMovement;
import java.util.List;

public interface StockMovementRepository {
    StockMovement save(StockMovement movement);
    List<StockMovement> findByBatchId(Long batchId);
}

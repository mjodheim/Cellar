package be.mjodheim.cellar.inventory.internal.application.port;

import be.mjodheim.cellar.inventory.internal.domain.Batch;
import java.util.List;
import java.util.Optional;

public interface BatchRepository {
    Batch save(Batch batch);
    Optional<Batch> findById(Long id);
    List<Batch> findByProductIdFefo(Long productId);
    boolean existsByProductIdAndLotNumberIgnoreCase(Long productId, String lotNumber);
}

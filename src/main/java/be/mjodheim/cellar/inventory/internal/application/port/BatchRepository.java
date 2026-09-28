package be.mjodheim.cellar.inventory.internal.application.port;

import be.mjodheim.cellar.inventory.internal.domain.Batch;

import java.util.List;
import java.util.Optional;

/**
 * Outbound persistence port for physical inventory batches.
 */
public interface BatchRepository {

    /**
     * Persists a batch.
     *
     * @param batch batch to save
     * @return persisted batch
     */
    Batch save(Batch batch);

    /**
     * Finds a non-deleted batch by identifier.
     *
     * @param id batch identifier
     * @return matching batch or an empty optional
     */
    Optional<Batch> findById(Long id);

    /**
     * Lists available batches for a product in FEFO order.
     *
     * @param productId product identifier
     * @return available batches
     */
    List<Batch> findByProductIdFefo(Long productId);

    /**
     * Checks active lot-number uniqueness within a product.
     *
     * @param productId product identifier
     * @param lotNumber lot reference
     * @return {@code true} when a matching active batch exists
     */
    boolean existsByProductIdAndLotNumberIgnoreCase(Long productId, String lotNumber);
}

package be.mjodheim.cellar.inventory.internal.application;

import be.mjodheim.cellar.inventory.internal.application.port.BatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Controls lifecycle operations that affect a batch as a whole, including safe soft deletion.
 */
@Service
public class BatchLifecycleService {

    private final BatchRepository batchRepository;

    /**
     * Creates the batch lifecycle service.
     *
     * @param batchRepository batch persistence boundary
     */
    public BatchLifecycleService(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    /**
     * Soft-deletes an empty batch.
     *
     * @param id batch identifier
     * @throws BatchNotFoundException when the batch does not exist
     * @throws IllegalStateException when stock or reservations remain
     */
    @Transactional
    public void softDelete(Long id) {
        var batch = batchRepository.findById(id).orElseThrow(() -> new BatchNotFoundException(id));
        batch.softDelete(Instant.now());
        batchRepository.save(batch);
    }
}

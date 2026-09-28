package be.mjodheim.cellar.inventory.internal.application;

import be.mjodheim.cellar.inventory.internal.application.port.BatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
/**
 * Controls lifecycle operations that affect a batch as a whole, including safe soft deletion.
 */
public class BatchLifecycleService {

    private final BatchRepository batchRepository;

    public BatchLifecycleService(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    @Transactional
    public void softDelete(Long id) {
        var batch = batchRepository.findById(id).orElseThrow(() -> new BatchNotFoundException(id));
        batch.softDelete(Instant.now());
        batchRepository.save(batch);
    }
}

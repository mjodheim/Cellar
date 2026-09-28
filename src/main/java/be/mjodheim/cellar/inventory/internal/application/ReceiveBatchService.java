package be.mjodheim.cellar.inventory.internal.application;

import be.mjodheim.cellar.inventory.internal.application.port.BatchRepository;
import be.mjodheim.cellar.inventory.internal.application.port.StockMovementRepository;
import be.mjodheim.cellar.inventory.internal.domain.Batch;
import be.mjodheim.cellar.inventory.internal.domain.StockMovement;
import be.mjodheim.cellar.inventory.internal.domain.StockMovementType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;

@Service
/**
 * Receives a physical batch and records the corresponding immutable receipt movement
 * in the same transaction.
 */
public class ReceiveBatchService {

    private final BatchRepository batchRepository;
    private final StockMovementRepository movementRepository;

    public ReceiveBatchService(BatchRepository batchRepository, StockMovementRepository movementRepository) {
        this.batchRepository = batchRepository;
        this.movementRepository = movementRepository;
    }

    @Transactional
    public Batch receive(Long productId, String lotNumber, int quantity, Instant receivedAt, LocalDate expiresOn) {
        if (batchRepository.existsByProductIdAndLotNumberIgnoreCase(productId, lotNumber)) {
            throw new BatchAlreadyExistsException(productId, lotNumber);
        }

        Instant now = Instant.now();
        Batch saved = batchRepository.save(Batch.receive(productId, lotNumber, quantity, receivedAt, expiresOn, now));

        movementRepository.save(StockMovement.record(
                saved.id(),
                StockMovementType.RECEIPT,
                quantity,
                saved.lotNumber(),
                "Initial batch reception",
                receivedAt,
                now
        ));

        return saved;
    }
}

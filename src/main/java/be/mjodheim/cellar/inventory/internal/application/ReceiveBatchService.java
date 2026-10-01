package be.mjodheim.cellar.inventory.internal.application;

import be.mjodheim.cellar.catalog.CatalogProducts;
import be.mjodheim.cellar.inventory.internal.application.port.BatchRepository;
import be.mjodheim.cellar.inventory.internal.application.port.StockMovementRepository;
import be.mjodheim.cellar.inventory.internal.domain.Batch;
import be.mjodheim.cellar.inventory.internal.domain.StockMovement;
import be.mjodheim.cellar.inventory.internal.domain.StockMovementType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Receives a physical batch and records the corresponding immutable receipt movement
 * in the same transaction.
 */
@Service
public class ReceiveBatchService {

    private final BatchRepository batchRepository;
    private final StockMovementRepository movementRepository;
    private final CatalogProducts catalogProducts;

    /**
     * Creates the batch reception service.
     *
     * @param batchRepository batch persistence boundary
     * @param movementRepository movement persistence boundary
     * @param catalogProducts public catalogue boundary used to validate the product
     */
    public ReceiveBatchService(BatchRepository batchRepository, StockMovementRepository movementRepository,
                               CatalogProducts catalogProducts) {
        this.batchRepository = batchRepository;
        this.movementRepository = movementRepository;
        this.catalogProducts = catalogProducts;
    }

    /**
     * Receives and persists a new batch, then records its receipt movement.
     *
     * @param productId product identifier
     * @param lotNumber lot reference
     * @param quantity received quantity
     * @param receivedAt reception timestamp
     * @param expiresOn optional expiration date
     * @return persisted batch
     * @throws BatchAlreadyExistsException when the active lot already exists for the product
     */
    @Transactional
    public Batch receive(Long productId, String lotNumber, int quantity, Instant receivedAt, LocalDate expiresOn) {
        Batch batch = Batch.receive(productId, lotNumber, quantity, receivedAt, expiresOn, Instant.now());
        catalogProducts.getProduct(productId);
        if (batchRepository.existsByProductIdAndLotNumberIgnoreCase(productId, batch.lotNumber())) {
            throw new BatchAlreadyExistsException(productId, batch.lotNumber());
        }

        Instant now = batch.createdAt();
        Batch saved = batchRepository.save(batch);

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

package be.mjodheim.cellar.inventory.internal.application;

import be.mjodheim.cellar.catalog.CatalogProducts;
import be.mjodheim.cellar.catalog.ProductNotFoundException;
import be.mjodheim.cellar.inventory.internal.application.port.BatchRepository;
import be.mjodheim.cellar.inventory.internal.application.port.StockMovementRepository;
import be.mjodheim.cellar.inventory.internal.domain.Batch;
import be.mjodheim.cellar.inventory.internal.domain.StockMovement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceiveBatchServiceTest {

    @Mock BatchRepository batchRepository;
    @Mock StockMovementRepository movementRepository;
    @Mock CatalogProducts catalogProducts;

    @Test
    void shouldSaveBatchAndReceiptMovement() {
        ReceiveBatchService service = new ReceiveBatchService(batchRepository, movementRepository, catalogProducts);

        when(batchRepository.existsByProductIdAndLotNumberIgnoreCase(1L, "LOT-001")).thenReturn(false);
        when(batchRepository.save(any(Batch.class))).thenAnswer(invocation -> {
            Batch b = invocation.getArgument(0);
            return Batch.rehydrate(10L, b.productId(), b.lotNumber(), b.receivedQuantity(),
                    b.quantityOnHand(), b.quantityReserved(), b.receivedAt(), b.expiresOn(),
                    b.createdAt(), b.updatedAt(), b.deletedAt());
        });
        when(movementRepository.save(any(StockMovement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Batch batch = service.receive(1L, "LOT-001", 50, Instant.parse("2026-09-28T10:00:00Z"), null);

        assertEquals(10L, batch.id());
        verify(batchRepository).save(any(Batch.class));
        verify(movementRepository).save(any(StockMovement.class));
    }

    @Test
    void shouldRejectAnUnknownProductBeforePersistingItsBatch() {
        when(catalogProducts.getProduct(999L)).thenThrow(new ProductNotFoundException(999L));

        assertThrows(ProductNotFoundException.class, () ->
                new ReceiveBatchService(batchRepository, movementRepository, catalogProducts)
                        .receive(999L, "LOT-001", 50, Instant.now(), null));

        verify(batchRepository, never()).save(any());
        verifyNoInteractions(movementRepository);
    }

    @Test
    void shouldRejectDuplicateLotForProduct() {
        ReceiveBatchService service = new ReceiveBatchService(batchRepository, movementRepository, catalogProducts);
        when(batchRepository.existsByProductIdAndLotNumberIgnoreCase(1L, "LOT-001")).thenReturn(true);

        assertThrows(BatchAlreadyExistsException.class,
                () -> service.receive(1L, "LOT-001", 50, Instant.now(), null));

        verify(batchRepository, never()).save(any());
        verify(movementRepository, never()).save(any());
    }
}

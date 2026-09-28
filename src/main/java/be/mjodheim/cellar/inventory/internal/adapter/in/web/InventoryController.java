package be.mjodheim.cellar.inventory.internal.adapter.in.web;

import be.mjodheim.cellar.inventory.internal.application.*;
import be.mjodheim.cellar.inventory.internal.domain.Batch;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@Tag(name = "Inventory", description = "Gestion des lots et des mouvements de stock")
class InventoryController {

    private final ReceiveBatchService receiveBatchService;
    private final InventoryQueryService queryService;
    private final BatchLifecycleService lifecycleService;

    InventoryController(
            ReceiveBatchService receiveBatchService,
            InventoryQueryService queryService,
            BatchLifecycleService lifecycleService
    ) {
        this.receiveBatchService = receiveBatchService;
        this.queryService = queryService;
        this.lifecycleService = lifecycleService;
    }

    @PostMapping("/batches")
    @Operation(summary = "Réceptionner un lot")
    @ApiResponse(responseCode = "201", description = "Lot créé et mouvement de réception enregistré")
    ResponseEntity<BatchResponse> receive(@Valid @RequestBody BatchReceiveRequest request) {
        Batch batch = receiveBatchService.receive(
                request.productId(),
                request.lotNumber(),
                request.quantity(),
                request.receivedAt(),
                request.expiresOn()
        );

        return ResponseEntity
                .created(URI.create("/api/inventory/batches/" + batch.id()))
                .body(BatchResponse.from(batch));
    }

    @GetMapping("/batches/{id}")
    @Operation(summary = "Consulter un lot")
    BatchResponse findById(@PathVariable Long id) {
        return BatchResponse.from(queryService.findBatchById(id));
    }

    @GetMapping("/products/{productId}/batches")
    @Operation(summary = "Lister les lots disponibles d'un produit selon l'ordre FEFO")
    List<BatchResponse> findForProduct(@PathVariable Long productId) {
        return queryService.findBatchesForProduct(productId).stream()
                .map(BatchResponse::from)
                .toList();
    }

    @GetMapping("/batches/{id}/movements")
    @Operation(summary = "Consulter l'historique des mouvements d'un lot")
    List<StockMovementResponse> movements(@PathVariable Long id) {
        return queryService.findMovements(id).stream()
                .map(StockMovementResponse::from)
                .toList();
    }

    @DeleteMapping("/batches/{id}")
    @Operation(summary = "Supprimer logiquement un lot vide")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void softDelete(@PathVariable Long id) {
        lifecycleService.softDelete(id);
    }

    @ExceptionHandler(BatchNotFoundException.class)
    ProblemDetail handleNotFound(BatchNotFoundException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Batch not found");
        problem.setDetail(exception.getMessage());
        return problem;
    }

    @ExceptionHandler(BatchAlreadyExistsException.class)
    ProblemDetail handleConflict(BatchAlreadyExistsException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problem.setTitle("Batch already exists");
        problem.setDetail(exception.getMessage());
        return problem;
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    ProblemDetail handleBusinessError(RuntimeException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        problem.setTitle("Inventory rule violation");
        problem.setDetail(exception.getMessage());
        return problem;
    }
}

package be.mjodheim.cellar.ordering.internal.adapter.in.web;

import be.mjodheim.cellar.ordering.internal.application.*;
import be.mjodheim.cellar.ordering.internal.domain.Order;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Gestion des commandes")
class OrderController {

    private final CreateOrderService createOrderService;
    private final OrderQueryService queryService;
    private final OrderLifecycleService lifecycleService;

    OrderController(
            CreateOrderService createOrderService,
            OrderQueryService queryService,
            OrderLifecycleService lifecycleService
    ) {
        this.createOrderService = createOrderService;
        this.queryService = queryService;
        this.lifecycleService = lifecycleService;
    }

    @PostMapping
    @Operation(summary = "Créer une commande en brouillon")
    ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        Order order = createOrderService.create(
                request.customerReference(),
                request.lines().stream()
                        .map(line -> new CreateOrderLineCommand(line.productId(), line.quantity()))
                        .toList()
        );

        return ResponseEntity
                .created(URI.create("/api/orders/" + order.id()))
                .body(OrderResponse.from(order));
    }

    @GetMapping
    @Operation(summary = "Lister les commandes")
    List<OrderResponse> findAll() {
        return queryService.findAll().stream().map(OrderResponse::from).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter une commande")
    OrderResponse findById(@PathVariable Long id) {
        return OrderResponse.from(queryService.findById(id));
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "Confirmer une commande et réserver le stock en FEFO")
    OrderResponse confirm(@PathVariable Long id) {
        return OrderResponse.from(lifecycleService.confirm(id));
    }

    @PostMapping("/{id}/prepare")
    @Operation(summary = "Passer une commande en préparation")
    OrderResponse prepare(@PathVariable Long id) {
        return OrderResponse.from(lifecycleService.startPreparation(id));
    }

    @PostMapping("/{id}/ship")
    @Operation(summary = "Expédier la commande et consommer les réservations")
    OrderResponse ship(@PathVariable Long id) {
        return OrderResponse.from(lifecycleService.ship(id));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Annuler la commande et libérer les réservations")
    OrderResponse cancel(@PathVariable Long id) {
        return OrderResponse.from(lifecycleService.cancel(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer logiquement une commande brouillon ou annulée")
    void softDelete(@PathVariable Long id) {
        lifecycleService.softDelete(id);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    ProblemDetail handleNotFound(OrderNotFoundException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Order not found");
        problem.setDetail(exception.getMessage());
        return problem;
    }

    @ExceptionHandler(ProductUnavailableException.class)
    ProblemDetail handleUnavailable(ProductUnavailableException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problem.setTitle("Product unavailable");
        problem.setDetail(exception.getMessage());
        return problem;
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    ProblemDetail handleBusinessError(RuntimeException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        problem.setTitle("Order rule violation");
        problem.setDetail(exception.getMessage());
        return problem;
    }
}

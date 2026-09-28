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

/**
 * REST adapter exposing order creation, queries and lifecycle transitions.
 */
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

    /**
     * Creates a draft order from catalogue product references.
     *
     * @param request validated order creation payload
     * @return HTTP 201 response containing the created order
     */
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

    /** @return all non-deleted orders */
    @GetMapping
    @Operation(summary = "Lister les commandes")
    List<OrderResponse> findAll() {
        return queryService.findAll().stream().map(OrderResponse::from).toList();
    }

    /**
     * Retrieves one order.
     *
     * @param id order identifier
     * @return matching order
     */
    @GetMapping("/{id}")
    @Operation(summary = "Consulter une commande")
    OrderResponse findById(@PathVariable Long id) {
        return OrderResponse.from(queryService.findById(id));
    }

    /**
     * Confirms an order and reserves stock according to FEFO.
     *
     * @param id order identifier
     * @return confirmed order
     */
    @PostMapping("/{id}/confirm")
    @Operation(summary = "Confirmer une commande et réserver le stock en FEFO")
    OrderResponse confirm(@PathVariable Long id) {
        return OrderResponse.from(lifecycleService.confirm(id));
    }

    /**
     * Moves a confirmed order into preparation.
     *
     * @param id order identifier
     * @return updated order
     */
    @PostMapping("/{id}/prepare")
    @Operation(summary = "Passer une commande en préparation")
    OrderResponse prepare(@PathVariable Long id) {
        return OrderResponse.from(lifecycleService.startPreparation(id));
    }

    /**
     * Ships an order and consumes reserved stock.
     *
     * @param id order identifier
     * @return shipped order
     */
    @PostMapping("/{id}/ship")
    @Operation(summary = "Expédier la commande et consommer les réservations")
    OrderResponse ship(@PathVariable Long id) {
        return OrderResponse.from(lifecycleService.ship(id));
    }

    /**
     * Cancels an order and releases active reservations when required.
     *
     * @param id order identifier
     * @return cancelled order
     */
    @PostMapping("/{id}/cancel")
    @Operation(summary = "Annuler la commande et libérer les réservations")
    OrderResponse cancel(@PathVariable Long id) {
        return OrderResponse.from(lifecycleService.cancel(id));
    }

    /**
     * Soft-deletes a draft or cancelled order.
     *
     * @param id order identifier
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer logiquement une commande brouillon ou annulée")
    void softDelete(@PathVariable Long id) {
        lifecycleService.softDelete(id);
    }

    /** Maps missing-order errors to HTTP 404. */
    @ExceptionHandler(OrderNotFoundException.class)
    ProblemDetail handleNotFound(OrderNotFoundException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Order not found");
        problem.setDetail(exception.getMessage());
        return problem;
    }

    /** Maps inactive-product errors to HTTP 409. */
    @ExceptionHandler(ProductUnavailableException.class)
    ProblemDetail handleUnavailable(ProductUnavailableException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problem.setTitle("Product unavailable");
        problem.setDetail(exception.getMessage());
        return problem;
    }

    /** Maps order rule violations to HTTP 422. */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    ProblemDetail handleBusinessError(RuntimeException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        problem.setTitle("Order rule violation");
        problem.setDetail(exception.getMessage());
        return problem;
    }
}

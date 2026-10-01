package be.mjodheim.cellar.ordering.internal.application;

import be.mjodheim.cellar.inventory.InventoryOperations;
import be.mjodheim.cellar.ordering.internal.application.port.OrderRepository;
import be.mjodheim.cellar.ordering.internal.domain.Order;
import be.mjodheim.cellar.ordering.internal.domain.OrderLine;
import be.mjodheim.cellar.ordering.internal.domain.OrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

/**
 * Coordinates order status transitions with Inventory reservations and shipment consumption.
 *
 * <p>Because Cellar is a monolith, these cross-module operations participate in the
 * same transaction, preserving consistency if allocation or shipment fails.</p>
 */
@Service
public class OrderLifecycleService {

    private final OrderRepository orderRepository;
    private final InventoryOperations inventoryOperations;

    /**
     * Creates the lifecycle coordinator.
     *
     * @param orderRepository order persistence boundary
     * @param inventoryOperations public Inventory module API
     */
    public OrderLifecycleService(OrderRepository orderRepository, InventoryOperations inventoryOperations) {
        this.orderRepository = orderRepository;
        this.inventoryOperations = inventoryOperations;
    }

    /**
     * Confirms a draft order and allocates stock for each line.
     *
     * @param id order identifier
     * @return confirmed order
     */
    @Transactional
    public Order confirm(Long id) {
        Order order = get(id);
        order.confirm(Instant.now());
        Order saved = orderRepository.save(order);

        for (var line : orderedLines(saved)) {
            inventoryOperations.allocate(line.id(), line.productId(), line.quantity());
        }

        return saved;
    }

    /**
     * Moves a confirmed order into preparation.
     *
     * @param id order identifier
     * @return updated order
     */
    @Transactional
    public Order startPreparation(Long id) {
        Order order = get(id);
        order.startPreparation(Instant.now());
        return orderRepository.save(order);
    }

    /**
     * Ships an order and consumes all reserved allocations.
     *
     * @param id order identifier
     * @return shipped order
     */
    @Transactional
    public Order ship(Long id) {
        Order order = get(id);

        for (var line : orderedLines(order)) {
            inventoryOperations.consume(line.id());
        }

        order.ship(Instant.now());
        return orderRepository.save(order);
    }

    /**
     * Cancels an order and releases reservations when necessary.
     *
     * @param id order identifier
     * @return cancelled order
     */
    @Transactional
    public Order cancel(Long id) {
        Order order = get(id);

        if (order.status() == OrderStatus.CONFIRMED || order.status() == OrderStatus.PREPARING) {
            for (var line : orderedLines(order)) {
                inventoryOperations.release(line.id());
            }
        }

        order.cancel(Instant.now());
        return orderRepository.save(order);
    }

    /**
     * Soft-deletes an order when the domain lifecycle allows it.
     *
     * @param id order identifier
     */
    @Transactional
    public void softDelete(Long id) {
        Order order = get(id);
        order.softDelete(Instant.now());
        orderRepository.save(order);
    }

    private Order get(Long id) {
        return orderRepository.findByIdForUpdate(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    // Use the same product order for confirmation, cancellation and shipment.
    private static List<OrderLine> orderedLines(Order order) {
        return order.lines().stream()
                .sorted(Comparator.comparing(OrderLine::productId).thenComparing(OrderLine::id))
                .toList();
    }
}

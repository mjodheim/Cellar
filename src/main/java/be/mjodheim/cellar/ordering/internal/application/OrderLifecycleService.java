package be.mjodheim.cellar.ordering.internal.application;

import be.mjodheim.cellar.inventory.InventoryOperations;
import be.mjodheim.cellar.ordering.internal.application.port.OrderRepository;
import be.mjodheim.cellar.ordering.internal.domain.Order;
import be.mjodheim.cellar.ordering.internal.domain.OrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class OrderLifecycleService {

    private final OrderRepository orderRepository;
    private final InventoryOperations inventoryOperations;

    public OrderLifecycleService(OrderRepository orderRepository, InventoryOperations inventoryOperations) {
        this.orderRepository = orderRepository;
        this.inventoryOperations = inventoryOperations;
    }

    @Transactional
    public Order confirm(Long id) {
        Order order = get(id);
        order.confirm(Instant.now());
        Order saved = orderRepository.save(order);

        for (var line : saved.lines()) {
            inventoryOperations.allocate(line.id(), line.productId(), line.quantity());
        }

        return saved;
    }

    @Transactional
    public Order startPreparation(Long id) {
        Order order = get(id);
        order.startPreparation(Instant.now());
        return orderRepository.save(order);
    }

    @Transactional
    public Order ship(Long id) {
        Order order = get(id);

        for (var line : order.lines()) {
            inventoryOperations.consume(line.id());
        }

        order.ship(Instant.now());
        return orderRepository.save(order);
    }

    @Transactional
    public Order cancel(Long id) {
        Order order = get(id);

        if (order.status() == OrderStatus.CONFIRMED || order.status() == OrderStatus.PREPARING) {
            for (var line : order.lines()) {
                inventoryOperations.release(line.id());
            }
        }

        order.cancel(Instant.now());
        return orderRepository.save(order);
    }

    @Transactional
    public void softDelete(Long id) {
        Order order = get(id);
        order.softDelete(Instant.now());
        orderRepository.save(order);
    }

    private Order get(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }
}

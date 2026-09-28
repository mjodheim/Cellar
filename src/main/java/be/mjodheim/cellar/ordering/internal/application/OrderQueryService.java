package be.mjodheim.cellar.ordering.internal.application;

import be.mjodheim.cellar.ordering.internal.application.port.OrderRepository;
import be.mjodheim.cellar.ordering.internal.domain.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Read-only Ordering use cases for listing and retrieving non-deleted orders.
 */
@Service
public class OrderQueryService {

    private final OrderRepository orderRepository;

    /**
     * Creates the query service.
     *
     * @param orderRepository order persistence boundary
     */
    public OrderQueryService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Retrieves one non-deleted order.
     *
     * @param id order identifier
     * @return matching order
     * @throws OrderNotFoundException when no order exists for the identifier
     */
    @Transactional(readOnly = true)
    public Order findById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    /**
     * Lists all non-deleted orders.
     *
     * @return orders in repository-defined order
     */
    @Transactional(readOnly = true)
    public List<Order> findAll() {
        return orderRepository.findAll();
    }
}

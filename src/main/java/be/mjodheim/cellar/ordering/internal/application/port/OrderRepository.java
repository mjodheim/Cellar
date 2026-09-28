package be.mjodheim.cellar.ordering.internal.application.port;

import be.mjodheim.cellar.ordering.internal.domain.Order;

import java.util.List;
import java.util.Optional;

/**
 * Outbound persistence port for the Ordering aggregate.
 */
public interface OrderRepository {

    /**
     * Persists an order aggregate.
     *
     * @param order order to save
     * @return persisted order
     */
    Order save(Order order);

    /**
     * Finds a non-deleted order by identifier.
     *
     * @param id order identifier
     * @return matching order or an empty optional
     */
    Optional<Order> findById(Long id);

    /**
     * Lists all non-deleted orders.
     *
     * @return orders in repository-defined order
     */
    List<Order> findAll();
}

package be.mjodheim.cellar.ordering.internal.application.port;

import be.mjodheim.cellar.ordering.internal.domain.Order;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long id);
    List<Order> findAll();
}

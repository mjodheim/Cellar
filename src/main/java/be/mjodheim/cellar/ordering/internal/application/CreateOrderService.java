package be.mjodheim.cellar.ordering.internal.application;

import be.mjodheim.cellar.catalog.CatalogProducts;
import be.mjodheim.cellar.ordering.internal.application.port.OrderRepository;
import be.mjodheim.cellar.ordering.internal.domain.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
/**
 * Creates draft orders from catalogue snapshots exposed through the Catalog module API.
 */
public class CreateOrderService {

    private final OrderRepository orderRepository;
    private final CatalogProducts catalogProducts;

    public CreateOrderService(OrderRepository orderRepository, CatalogProducts catalogProducts) {
        this.orderRepository = orderRepository;
        this.catalogProducts = catalogProducts;
    }

    @Transactional
    public Order create(String customerReference, List<CreateOrderLineCommand> lines) {
        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException("An order must contain at least one requested line");
        }

        Instant now = Instant.now();
        Order order = Order.create("ORD-" + UUID.randomUUID(), customerReference, now);

        for (CreateOrderLineCommand line : lines) {
            if (line == null) {
                throw new IllegalArgumentException("Order line is required");
            }

            var product = catalogProducts.getProduct(line.productId());
            if (!product.active()) {
                throw new ProductUnavailableException(product.id());
            }

            order.addLine(
                    product.id(),
                    product.name(),
                    line.quantity(),
                    product.price(),
                    now
            );
        }

        return orderRepository.save(order);
    }
}

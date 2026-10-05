package be.mjodheim.cellar.ordering.internal.application;

import be.mjodheim.cellar.catalog.CatalogProductView;
import be.mjodheim.cellar.catalog.CatalogProducts;
import be.mjodheim.cellar.ordering.internal.application.port.OrderRepository;
import be.mjodheim.cellar.ordering.internal.domain.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Creates draft orders from catalogue snapshots exposed through the Catalog module API.
 */
@Service
public class CreateOrderService {

    private final OrderRepository orderRepository;
    private final CatalogProducts catalogProducts;

    /**
     * Creates the order-creation use case.
     *
     * @param orderRepository order persistence boundary
     * @param catalogProducts public Catalog module API
     */
    public CreateOrderService(OrderRepository orderRepository, CatalogProducts catalogProducts) {
        this.orderRepository = orderRepository;
        this.catalogProducts = catalogProducts;
    }

    /**
     * Creates a draft order and snapshots current catalogue data into each line.
     *
     * @param customerReference optional external customer reference
     * @param lines requested product/quantity lines
     * @return persisted draft order
     * @throws IllegalArgumentException when no valid line is supplied
     * @throws ProductUnavailableException when a requested product is inactive
     */
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

            CatalogProductView product = catalogProducts.getProduct(line.productId());
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

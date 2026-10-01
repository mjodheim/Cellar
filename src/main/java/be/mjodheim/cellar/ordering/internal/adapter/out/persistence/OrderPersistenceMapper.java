package be.mjodheim.cellar.ordering.internal.adapter.out.persistence;

import be.mjodheim.cellar.ordering.internal.domain.Order;
import be.mjodheim.cellar.ordering.internal.domain.OrderLine;

/**
 * Bidirectional mapper between Ordering domain objects and JPA entities.
 */
final class OrderPersistenceMapper {

    private OrderPersistenceMapper() {}

    /**
     * Converts an order aggregate to its persistence representation.
     *
     * @param order domain order
     * @return order entity with attached line entities
     */
    static OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity(
                order.id(),
                order.orderNumber(),
                order.customerReference(),
                order.status(),
                order.createdAt(),
                order.updatedAt(),
                order.deletedAt()
        );

        for (OrderLine line : order.lines()) {
            entity.addLine(new OrderLineEntity(
                    line.id(),
                    line.productId(),
                    line.productName(),
                    line.quantity(),
                    line.unitPrice(),
                    line.createdAt()
            ));
        }

        return entity;
    }

    /**
     * Rehydrates an order aggregate and its lines from persisted values.
     *
     * @param entity order persistence entity
     * @return domain order
     */
    static Order toDomain(OrderEntity entity) {
        return Order.rehydrate(
                entity.id(),
                entity.orderNumber(),
                entity.customerReference(),
                entity.status(),
                entity.lines().stream()
                        .map(line -> OrderLine.rehydrate(
                                line.id(),
                                line.productId(),
                                line.productName(),
                                line.quantity(),
                                line.unitPrice(),
                                line.createdAt()
                        ))
                        .toList(),
                entity.createdAt(),
                entity.updatedAt(),
                entity.deletedAt()
        );
    }
}

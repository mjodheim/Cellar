package be.mjodheim.cellar.ordering.internal.adapter.out.persistence;

import be.mjodheim.cellar.ordering.internal.domain.Order;
import be.mjodheim.cellar.ordering.internal.domain.OrderLine;

final class OrderPersistenceMapper {

    private OrderPersistenceMapper() {}

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

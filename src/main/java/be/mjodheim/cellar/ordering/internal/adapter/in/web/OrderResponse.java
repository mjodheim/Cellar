package be.mjodheim.cellar.ordering.internal.adapter.in.web;

import be.mjodheim.cellar.ordering.internal.domain.Order;
import be.mjodheim.cellar.ordering.internal.domain.OrderLine;
import be.mjodheim.cellar.ordering.internal.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

record OrderResponse(
        Long id,
        String orderNumber,
        String customerReference,
        OrderStatus status,
        List<Line> lines,
        BigDecimal total,
        Instant createdAt,
        Instant updatedAt
) {
    record Line(
            Long id,
            Long productId,
            String productName,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal total
    ) {
        static Line from(OrderLine line) {
            return new Line(
                    line.id(),
                    line.productId(),
                    line.productName(),
                    line.quantity(),
                    line.unitPrice(),
                    line.total()
            );
        }
    }

    static OrderResponse from(Order order) {
        return new OrderResponse(
                order.id(),
                order.orderNumber(),
                order.customerReference(),
                order.status(),
                order.lines().stream().map(Line::from).toList(),
                order.total(),
                order.createdAt(),
                order.updatedAt()
        );
    }
}

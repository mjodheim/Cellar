package be.mjodheim.cellar.ordering.internal.adapter.in.web;

import be.mjodheim.cellar.ordering.internal.domain.Order;
import be.mjodheim.cellar.ordering.internal.domain.OrderLine;
import be.mjodheim.cellar.ordering.internal.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * HTTP representation of a customer order.
 *
 * @param id order identifier
 * @param orderNumber immutable order number
 * @param customerReference optional customer reference
 * @param status current order lifecycle state
 * @param lines commercial snapshot lines
 * @param total calculated order total
 * @param createdAt creation timestamp
 * @param updatedAt last modification timestamp
 */
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

    /**
     * HTTP representation of one order line.
     *
     * @param id line identifier
     * @param productId catalogue product identifier
     * @param productName product name snapshot
     * @param quantity ordered quantity
     * @param unitPrice unit price snapshot
     * @param total calculated line total
     */
    record Line(
            Long id,
            Long productId,
            String productName,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal total
    ) {
        /**
         * Maps a domain order line to its HTTP representation.
         *
         * @param line domain order line
         * @return response projection
         */
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

    /**
     * Maps a domain order to its HTTP representation.
     *
     * @param order domain order
     * @return response projection
     */
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

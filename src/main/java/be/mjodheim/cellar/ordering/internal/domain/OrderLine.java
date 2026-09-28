package be.mjodheim.cellar.ordering.internal.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * Immutable commercial snapshot of a product inside an order.
 *
 * <p>The product name and unit price are copied at order creation time so
 * historical orders remain accurate even if the catalogue changes later.</p>
 */
public final class OrderLine {

    private final Long id;
    private final Long productId;
    private final String productName;
    private final int quantity;
    private final BigDecimal unitPrice;
    private final Instant createdAt;

    private OrderLine(
            Long id,
            Long productId,
            String productName,
            int quantity,
            BigDecimal unitPrice,
            Instant createdAt
    ) {
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("Product id must be positive");
        }
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Order line quantity must be greater than zero");
        }
        if (unitPrice == null || unitPrice.signum() < 0) {
            throw new IllegalArgumentException("Unit price cannot be negative");
        }

        this.id = id;
        this.productId = productId;
        this.productName = productName.trim();
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public static OrderLine create(
            Long productId,
            String productName,
            int quantity,
            BigDecimal unitPrice,
            Instant now
    ) {
        return new OrderLine(null, productId, productName, quantity, unitPrice, now);
    }

    public static OrderLine rehydrate(
            Long id,
            Long productId,
            String productName,
            int quantity,
            BigDecimal unitPrice,
            Instant createdAt
    ) {
        return new OrderLine(id, productId, productName, quantity, unitPrice, createdAt);
    }

    public BigDecimal total() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public Long id(){ return id; }
    public Long productId(){ return productId; }
    public String productName(){ return productName; }
    public int quantity(){ return quantity; }
    public BigDecimal unitPrice(){ return unitPrice; }
    public Instant createdAt(){ return createdAt; }
}

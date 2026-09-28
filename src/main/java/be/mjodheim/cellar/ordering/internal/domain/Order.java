package be.mjodheim.cellar.ordering.internal.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Ordering aggregate controlling the lifecycle of a customer order.
 *
 * <p>The aggregate owns its lines, status transitions and soft-delete rules.
 * Stock allocation itself is delegated to the Inventory module through its
 * public module API.</p>
 */
public final class Order {

    private final Long id;
    private final String orderNumber;
    private final String customerReference;
    private final Instant createdAt;
    private final List<OrderLine> lines;

    private OrderStatus status;
    private Instant updatedAt;
    private Instant deletedAt;

    private Order(
            Long id,
            String orderNumber,
            String customerReference,
            OrderStatus status,
            List<OrderLine> lines,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt
    ) {
        if (orderNumber == null || orderNumber.isBlank()) {
            throw new IllegalArgumentException("Order number is required");
        }

        this.id = id;
        this.orderNumber = orderNumber.trim();
        this.customerReference = normalize(customerReference);
        this.status = Objects.requireNonNull(status);
        this.lines = new ArrayList<>(Objects.requireNonNull(lines));
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
        this.deletedAt = deletedAt;
    }

    public static Order create(String orderNumber, String customerReference, Instant now) {
        return new Order(null, orderNumber, customerReference, OrderStatus.DRAFT, List.of(), now, now, null);
    }

    public static Order rehydrate(
            Long id,
            String orderNumber,
            String customerReference,
            OrderStatus status,
            List<OrderLine> lines,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt
    ) {
        return new Order(id, orderNumber, customerReference, status, lines, createdAt, updatedAt, deletedAt);
    }

    public void addLine(Long productId, String productName, int quantity, BigDecimal unitPrice, Instant now) {
        requireStatus(OrderStatus.DRAFT, "Lines can only be added to a draft order");
        ensureNotDeleted();
        lines.add(OrderLine.create(productId, productName, quantity, unitPrice, now));
        updatedAt = Objects.requireNonNull(now);
    }

    public void confirm(Instant now) {
        requireStatus(OrderStatus.DRAFT, "Only a draft order can be confirmed");
        if (lines.isEmpty()) {
            throw new IllegalStateException("An order must contain at least one line before confirmation");
        }
        status = OrderStatus.CONFIRMED;
        updatedAt = Objects.requireNonNull(now);
    }

    public void startPreparation(Instant now) {
        requireStatus(OrderStatus.CONFIRMED, "Only a confirmed order can enter preparation");
        status = OrderStatus.PREPARING;
        updatedAt = Objects.requireNonNull(now);
    }

    public void ship(Instant now) {
        requireStatus(OrderStatus.PREPARING, "Only an order in preparation can be shipped");
        status = OrderStatus.SHIPPED;
        updatedAt = Objects.requireNonNull(now);
    }

    public void cancel(Instant now) {
        ensureNotDeleted();
        if (status == OrderStatus.SHIPPED) {
            throw new IllegalStateException("A shipped order cannot be cancelled");
        }
        if (status == OrderStatus.CANCELLED) {
            return;
        }
        status = OrderStatus.CANCELLED;
        updatedAt = Objects.requireNonNull(now);
    }

    public void softDelete(Instant now) {
        if (deletedAt != null) return;
        if (status != OrderStatus.DRAFT && status != OrderStatus.CANCELLED) {
            throw new IllegalStateException("Only draft or cancelled orders can be deleted");
        }
        deletedAt = Objects.requireNonNull(now);
        updatedAt = now;
    }

    public BigDecimal total() {
        return lines.stream().map(OrderLine::total).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean isDeleted() { return deletedAt != null; }

    private void requireStatus(OrderStatus expected, String message) {
        ensureNotDeleted();
        if (status != expected) throw new IllegalStateException(message);
    }

    private void ensureNotDeleted() {
        if (isDeleted()) throw new IllegalStateException("Deleted order cannot be modified");
    }

    private static String normalize(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public Long id(){ return id; }
    public String orderNumber(){ return orderNumber; }
    public String customerReference(){ return customerReference; }
    public OrderStatus status(){ return status; }
    public List<OrderLine> lines(){ return List.copyOf(lines); }
    public Instant createdAt(){ return createdAt; }
    public Instant updatedAt(){ return updatedAt; }
    public Instant deletedAt(){ return deletedAt; }
}

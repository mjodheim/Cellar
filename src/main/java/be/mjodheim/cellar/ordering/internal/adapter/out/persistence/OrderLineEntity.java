package be.mjodheim.cellar.ordering.internal.adapter.out.persistence;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * JPA representation of a persisted order-line commercial snapshot.
 */
@Entity
@Table(name = "order_line")
class OrderLineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name", nullable = false, length = 150)
    private String productName;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /** Constructor required by JPA. */
    protected OrderLineEntity() {}

    /**
     * Builds an order-line entity from explicit persisted values.
     */
    OrderLineEntity(Long id, Long productId, String productName, int quantity, BigDecimal unitPrice, Instant createdAt) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.createdAt = createdAt;
    }

    /**
     * Attaches this line to its owning order entity.
     *
     * @param order owning order entity
     */
    void attachTo(OrderEntity order) {
        this.order = order;
    }

    Long id(){ return id; }
    Long productId(){ return productId; }
    String productName(){ return productName; }
    int quantity(){ return quantity; }
    BigDecimal unitPrice(){ return unitPrice; }
    Instant createdAt(){ return createdAt; }
}

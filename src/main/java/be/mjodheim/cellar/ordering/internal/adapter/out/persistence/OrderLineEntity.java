package be.mjodheim.cellar.ordering.internal.adapter.out.persistence;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

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

    protected OrderLineEntity() {}

    OrderLineEntity(Long id, Long productId, String productName, int quantity, BigDecimal unitPrice, Instant createdAt) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.createdAt = createdAt;
    }

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

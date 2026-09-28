package be.mjodheim.cellar.ordering.internal.adapter.out.persistence;

import be.mjodheim.cellar.ordering.internal.domain.OrderStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer_order")
class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, length = 80)
    private String orderNumber;

    @Column(name = "customer_reference", length = 150)
    private String customerReference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<OrderLineEntity> lines = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    protected OrderEntity() {}

    OrderEntity(Long id, String orderNumber, String customerReference, OrderStatus status,
                Instant createdAt, Instant updatedAt, Instant deletedAt) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.customerReference = customerReference;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    void addLine(OrderLineEntity line) {
        lines.add(line);
        line.attachTo(this);
    }

    Long id(){ return id; }
    String orderNumber(){ return orderNumber; }
    String customerReference(){ return customerReference; }
    OrderStatus status(){ return status; }
    List<OrderLineEntity> lines(){ return lines; }
    Instant createdAt(){ return createdAt; }
    Instant updatedAt(){ return updatedAt; }
    Instant deletedAt(){ return deletedAt; }
}

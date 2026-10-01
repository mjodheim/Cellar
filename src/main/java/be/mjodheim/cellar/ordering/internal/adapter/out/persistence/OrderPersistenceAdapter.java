package be.mjodheim.cellar.ordering.internal.adapter.out.persistence;

import be.mjodheim.cellar.ordering.internal.application.port.OrderRepository;
import be.mjodheim.cellar.ordering.internal.domain.Order;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Persistence adapter implementing the Ordering repository port.
 */
@Repository
class OrderPersistenceAdapter implements OrderRepository {

    private final JpaOrderRepository repository;

    OrderPersistenceAdapter(JpaOrderRepository repository) {
        this.repository = repository;
    }

    /** {@inheritDoc} */
    @Override
    public Order save(Order order) {
        return OrderPersistenceMapper.toDomain(repository.save(OrderPersistenceMapper.toEntity(order)));
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Order> findById(Long id) {
        return repository.findByIdAndDeletedAtIsNull(id).map(OrderPersistenceMapper::toDomain);
    }

    /** {@inheritDoc} */
    @Override
    public List<Order> findAll() {
        return repository.findAllByDeletedAtIsNullOrderByIdDesc().stream()
                .map(OrderPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Order> findByIdForUpdate(Long id) {
        return repository.findByIdForUpdate(id).map(OrderPersistenceMapper::toDomain);
    }
}

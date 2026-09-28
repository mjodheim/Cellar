package be.mjodheim.cellar.ordering.internal.adapter.out.persistence;

import be.mjodheim.cellar.ordering.internal.application.port.OrderRepository;
import be.mjodheim.cellar.ordering.internal.domain.Order;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class OrderPersistenceAdapter implements OrderRepository {

    private final JpaOrderRepository repository;

    OrderPersistenceAdapter(JpaOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public Order save(Order order) {
        return OrderPersistenceMapper.toDomain(repository.save(OrderPersistenceMapper.toEntity(order)));
    }

    @Override
    public Optional<Order> findById(Long id) {
        return repository.findByIdAndDeletedAtIsNull(id).map(OrderPersistenceMapper::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return repository.findAllByDeletedAtIsNullOrderByIdDesc().stream()
                .map(OrderPersistenceMapper::toDomain)
                .toList();
    }
}

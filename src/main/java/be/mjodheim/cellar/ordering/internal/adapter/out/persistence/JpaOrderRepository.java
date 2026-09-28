package be.mjodheim.cellar.ordering.internal.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

interface JpaOrderRepository extends JpaRepository<OrderEntity, Long> {
    Optional<OrderEntity> findByIdAndDeletedAtIsNull(Long id);
    List<OrderEntity> findAllByDeletedAtIsNullOrderByIdDesc();
}

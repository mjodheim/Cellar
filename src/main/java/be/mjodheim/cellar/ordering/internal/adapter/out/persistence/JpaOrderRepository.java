package be.mjodheim.cellar.ordering.internal.adapter.out.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data repository for persisted orders.
 */
interface JpaOrderRepository extends JpaRepository<OrderEntity, Long> {

    /**
     * Finds a non-deleted order by identifier.
     *
     * @param id order identifier
     * @return matching order or an empty optional
     */
    Optional<OrderEntity> findByIdAndDeletedAtIsNull(Long id);

    /**
     * Lists non-deleted orders with newest identifiers first.
     *
     * @return ordered order entities
     */
    List<OrderEntity> findAllByDeletedAtIsNullOrderByIdDesc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from OrderEntity o where o.id = :id and o.deletedAt is null")
    Optional<OrderEntity> findByIdForUpdate(@Param("id") Long id);
}

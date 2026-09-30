package be.mjodheim.cellar.inventory.internal.adapter.out.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data repository for physical stock batches.
 */
interface JpaBatchRepository extends JpaRepository<BatchEntity, Long> {

    /**
     * Finds a non-deleted batch by identifier.
     *
     * @param id batch identifier
     * @return matching batch or an empty optional
     */
    Optional<BatchEntity> findByIdAndDeletedAtIsNull(Long id);

    /**
     * Checks active lot-number uniqueness within a product.
     *
     * @param productId product identifier
     * @param lotNumber lot reference
     * @return {@code true} when an active matching lot exists
     */
    boolean existsByProductIdAndLotNumberIgnoreCaseAndDeletedAtIsNull(Long productId, String lotNumber);

    /**
     * Returns available batches in FEFO order.
     *
     * @param productId product identifier
     * @return available batches ordered by expiration, reception and identifier
     */
    @Query("""
            select b from BatchEntity b
            where b.productId = :productId
              and b.deletedAt is null
              and b.quantityOnHand > b.quantityReserved
            order by
              case when b.expiresOn is null then 1 else 0 end,
              b.expiresOn asc,
              b.receivedAt asc,
              b.id asc
            """)
    List<BatchEntity> findAvailableByProductIdFefo(@Param("productId") Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from BatchEntity b where b.id = :id and b.deletedAt is null")
    Optional<BatchEntity> findByIdForUpdate(@Param("id") Long id);

    // Acquire locks in identifier order; the adapter applies FEFO afterwards.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select b from BatchEntity b
            where b.productId = :productId
              and b.deletedAt is null
              and b.quantityOnHand > b.quantityReserved
            order by b.id asc
            """)
    List<BatchEntity> findAvailableByProductIdForUpdate(@Param("productId") Long productId);
}

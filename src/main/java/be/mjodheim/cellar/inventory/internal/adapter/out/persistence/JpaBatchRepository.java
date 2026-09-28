package be.mjodheim.cellar.inventory.internal.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

interface JpaBatchRepository extends JpaRepository<BatchEntity, Long> {

    Optional<BatchEntity> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByProductIdAndLotNumberIgnoreCaseAndDeletedAtIsNull(Long productId, String lotNumber);

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
}

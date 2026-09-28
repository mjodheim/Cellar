package be.mjodheim.cellar.catalog.internal.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data repository dedicated to the JPA product persistence model.
 *
 * <p>This interface remains internal to the persistence adapter and must not leak
 * into the application or domain layers.</p>
 */
interface JpaProductRepository extends JpaRepository<ProductEntity, Long> {

    /**
     * Checks whether a product name already exists independently of case.
     *
     * @param name product name to check
     * @return {@code true} when a matching name exists
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Returns every persisted product ordered by ascending identifier.
     *
     * @return ordered product entities
     */
    List<ProductEntity> findAllByOrderByIdAsc();
}

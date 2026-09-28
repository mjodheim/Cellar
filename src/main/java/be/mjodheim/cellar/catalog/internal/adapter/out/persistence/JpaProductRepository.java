package be.mjodheim.cellar.catalog.internal.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface JpaProductRepository extends JpaRepository<ProductEntity, Long> {

    boolean existsByNameIgnoreCase(String name);

    List<ProductEntity> findAllByOrderByIdAsc();
}

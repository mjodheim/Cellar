package be.mjodheim.cellar.catalog.internal.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface JpaProductRepository extends JpaRepository<ProductEntity, Long> {

    boolean existsByNameIgnoreCase(String name);
}

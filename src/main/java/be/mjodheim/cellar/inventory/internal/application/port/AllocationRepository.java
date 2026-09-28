package be.mjodheim.cellar.inventory.internal.application.port;

import be.mjodheim.cellar.inventory.internal.domain.Allocation;
import java.util.List;

public interface AllocationRepository {
    Allocation save(Allocation allocation);
    List<Allocation> findByOrderLineId(Long orderLineId);
}

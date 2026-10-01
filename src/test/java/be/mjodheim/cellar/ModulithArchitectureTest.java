package be.mjodheim.cellar;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModulithArchitectureTest {

    @Test
    void shouldRespectModuleBoundaries() {
        ApplicationModules.of(CellarApplication.class).verify();
    }
}

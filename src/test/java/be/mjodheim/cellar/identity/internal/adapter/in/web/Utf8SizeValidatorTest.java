package be.mjodheim.cellar.identity.internal.adapter.in.web;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Utf8SizeValidatorTest {

    record Password(@Utf8Size String value) {}

    @Test
    void acceptsExactly72Utf8Bytes() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            assertTrue(factory.getValidator().validate(new Password("a".repeat(68) + "😀")).isEmpty());
        }
    }

    @Test
    void rejectsMultibytePasswordsAboveTheBcryptLimit() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            assertFalse(factory.getValidator().validate(new Password("a".repeat(69) + "😀")).isEmpty());
        }
    }
}

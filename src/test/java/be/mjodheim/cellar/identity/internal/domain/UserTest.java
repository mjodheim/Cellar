package be.mjodheim.cellar.identity.internal.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private static final Instant NOW = Instant.parse("2026-09-28T12:00:00Z");
    private static final Instant LATER = Instant.parse("2026-09-28T13:00:00Z");

    @Test
    void shouldNormalizeEmailAndCreateEnabledUser() {
        User user = User.register(
                "  ANTHONY@EXAMPLE.COM ",
                "Anthony",
                "hashed-password",
                Role.USER,
                NOW
        );

        assertEquals("anthony@example.com", user.email());
        assertEquals(Role.USER, user.role());
        assertTrue(user.enabled());
        assertFalse(user.isDeleted());
    }

    @Test
    void shouldRejectInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> User.register(
                "not-an-email",
                "Anthony",
                "hashed-password",
                Role.USER,
                NOW
        ));
    }

    @Test
    void shouldSoftDeleteAndDisableAccount() {
        User user = User.register(
                "anthony@example.com",
                "Anthony",
                "hashed-password",
                Role.USER,
                NOW
        );

        user.softDelete(LATER);

        assertTrue(user.isDeleted());
        assertFalse(user.enabled());
        assertEquals(LATER, user.deletedAt());
        assertThrows(IllegalStateException.class, () -> user.enable(LATER));
    }
}

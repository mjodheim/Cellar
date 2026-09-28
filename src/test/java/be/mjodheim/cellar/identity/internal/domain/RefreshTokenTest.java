package be.mjodheim.cellar.identity.internal.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenTest {

    private static final Instant NOW = Instant.parse("2026-09-28T12:00:00Z");
    private static final String HASH = "a".repeat(64);

    @Test
    void shouldBeActiveBeforeExpiration() {
        RefreshToken token = RefreshToken.issue(
                1L,
                HASH,
                NOW.plusSeconds(3600),
                NOW
        );

        assertTrue(token.isActiveAt(NOW.plusSeconds(30)));
    }

    @Test
    void shouldBecomeInactiveAfterRevocation() {
        RefreshToken token = RefreshToken.issue(
                1L,
                HASH,
                NOW.plusSeconds(3600),
                NOW
        );

        token.revoke(NOW.plusSeconds(60));

        assertFalse(token.isActiveAt(NOW.plusSeconds(61)));
    }

    @Test
    void shouldRejectExpiredTokenAtCreation() {
        assertThrows(IllegalArgumentException.class, () -> RefreshToken.issue(
                1L,
                HASH,
                NOW,
                NOW
        ));
    }
}

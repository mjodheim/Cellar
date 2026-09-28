package be.mjodheim.cellar.identity.internal.adapter.out.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpaqueRefreshTokenCodecTest {

    @Test
    void shouldGenerateDifferentOpaqueTokensAndStableHashes() {
        OpaqueRefreshTokenCodec codec = new OpaqueRefreshTokenCodec();

        var first = codec.generate();
        var second = codec.generate();

        assertNotEquals(first.rawToken(), second.rawToken());
        assertEquals(64, first.tokenHash().length());
        assertEquals(first.tokenHash(), codec.hash(first.rawToken()));
    }
}

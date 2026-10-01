package be.mjodheim.cellar.identity.internal.application;

import be.mjodheim.cellar.identity.internal.application.port.*;
import be.mjodheim.cellar.identity.internal.domain.RefreshToken;
import be.mjodheim.cellar.identity.internal.domain.Role;
import be.mjodheim.cellar.identity.internal.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock UserRepository userRepository;
    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock PasswordHashingPort passwordHashingPort;
    @Mock AccessTokenPort accessTokenPort;
    @Mock RefreshTokenCodec refreshTokenCodec;

    @Test
    void shouldRegisterAndIssueTokenPair() {
        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(passwordHashingPort.hash("very-secure-password")).thenReturn("hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return User.rehydrate(
                    1L,
                    user.email(),
                    user.displayName(),
                    user.passwordHash(),
                    user.role(),
                    user.enabled(),
                    user.createdAt(),
                    user.updatedAt(),
                    user.deletedAt()
            );
        });
        when(refreshTokenCodec.generate())
                .thenReturn(new RefreshTokenCodec.GeneratedRefreshToken("raw-refresh", "b".repeat(64)));
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(accessTokenPort.generate(any(User.class))).thenReturn("access-token");
        when(accessTokenPort.expiresInSeconds()).thenReturn(900L);

        AuthenticationService service = service();
        AuthenticationResult result = service.register(
                "user@example.com",
                "User",
                "very-secure-password"
        );

        assertEquals("access-token", result.accessToken());
        assertEquals("raw-refresh", result.refreshToken());
        assertEquals(Role.USER, result.role());
        verify(passwordHashingPort).hash("very-secure-password");
    }

    @Test
    void shouldRejectDuplicateRegistration() {
        when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

        assertThrows(
                EmailAlreadyRegisteredException.class,
                () -> service().register("user@example.com", "User", "very-secure-password")
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldRejectWrongPasswordWithoutRevealingDetails() {
        User user = existingUser();
        when(userRepository.findByEmailForUpdate("user@example.com")).thenReturn(Optional.of(user));
        when(passwordHashingPort.matches("wrong-password", "hash")).thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> service().login("user@example.com", "wrong-password")
        );
    }

    @Test
    void shouldRotateRefreshToken() {
        User user = existingUser();
        Instant now = Instant.now();
        RefreshToken current = RefreshToken.rehydrate(
                10L,
                1L,
                "c".repeat(64),
                now.plusSeconds(3600),
                now.minusSeconds(60),
                null
        );

        when(refreshTokenCodec.hash("old-refresh")).thenReturn("c".repeat(64));
        when(refreshTokenRepository.findUserIdByTokenHash("c".repeat(64))).thenReturn(Optional.of(1L));
        when(refreshTokenRepository.findByTokenHashForUpdate("c".repeat(64))).thenReturn(Optional.of(current));
        when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(user));
        when(refreshTokenCodec.generate())
                .thenReturn(new RefreshTokenCodec.GeneratedRefreshToken("new-refresh", "d".repeat(64)));
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(accessTokenPort.generate(user)).thenReturn("new-access");
        when(accessTokenPort.expiresInSeconds()).thenReturn(900L);

        AuthenticationResult result = service().refresh("old-refresh");

        assertNotNull(current.revokedAt());
        assertEquals("new-refresh", result.refreshToken());
        assertEquals("new-access", result.accessToken());
    }

    @Test
    void shouldRejectARevokedTokenReadUnderLock() {
        Instant now = Instant.now();
        when(refreshTokenCodec.hash("old-refresh")).thenReturn("c".repeat(64));
        when(refreshTokenRepository.findUserIdByTokenHash("c".repeat(64))).thenReturn(Optional.of(1L));
        when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(existingUser()));
        when(refreshTokenRepository.findByTokenHashForUpdate("c".repeat(64))).thenReturn(Optional.of(
                RefreshToken.rehydrate(10L, 1L, "c".repeat(64), now.plusSeconds(3600), now.minusSeconds(60), now)));

        assertThrows(InvalidRefreshTokenException.class, () -> service().refresh("old-refresh"));
        verify(refreshTokenCodec, never()).generate();
        verify(refreshTokenRepository, never()).save(any());
    }

    private AuthenticationService service() {
        return new AuthenticationService(
                userRepository,
                refreshTokenRepository,
                passwordHashingPort,
                accessTokenPort,
                refreshTokenCodec,
                Duration.ofDays(7)
        );
    }

    private static User existingUser() {
        Instant now = Instant.parse("2026-09-28T12:00:00Z");
        return User.rehydrate(
                1L,
                "user@example.com",
                "User",
                "hash",
                Role.USER,
                true,
                now,
                now,
                null
        );
    }
}

package be.mjodheim.cellar.identity.internal.application;

import be.mjodheim.cellar.identity.internal.application.port.RefreshTokenRepository;
import be.mjodheim.cellar.identity.internal.application.port.UserRepository;
import be.mjodheim.cellar.identity.internal.domain.RefreshToken;
import be.mjodheim.cellar.identity.internal.domain.Role;
import be.mjodheim.cellar.identity.internal.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAccountServiceTest {

    @Mock UserRepository users;
    @Mock RefreshTokenRepository tokens;

    @Test
    void profileLookupUsesTheImmutableId() {
        User user = user();
        when(users.findById(1L)).thenReturn(Optional.of(user));
        assertSame(user, new UserAccountService(users, tokens).findCurrent(1L));
        verify(users, never()).findByEmail(anyString());
    }

    @Test
    void aMissingOldAccountDoesNotFallBackToItsReusedEmail() {
        when(users.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> new UserAccountService(users, tokens).findCurrent(1L));
        verify(users, never()).findByEmail(anyString());
    }

    @Test
    void deletionLocksTheAccountBeforeRevokingItsSessions() {
        User user = user();
        Instant now = Instant.now();
        RefreshToken token = RefreshToken.issue(1L, "a".repeat(64), now.plusSeconds(3600), now);
        when(users.findByIdForUpdate(1L)).thenReturn(Optional.of(user));
        when(tokens.findActiveByUserId(1L)).thenReturn(List.of(token));

        new UserAccountService(users, tokens).deleteCurrent(1L);

        assertTrue(user.isDeleted());
        assertFalse(user.enabled());
        assertNotNull(token.revokedAt());
        var sequence = inOrder(users, tokens);
        sequence.verify(users).findByIdForUpdate(1L);
        sequence.verify(users).save(user);
        sequence.verify(tokens).findActiveByUserId(1L);
        sequence.verify(tokens).save(token);
    }

    private static User user() {
        Instant now = Instant.now();
        return User.rehydrate(1L, "user@example.com", "User", "hash", Role.USER, true, now, now, null);
    }
}

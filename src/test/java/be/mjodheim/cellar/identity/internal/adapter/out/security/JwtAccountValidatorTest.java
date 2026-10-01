package be.mjodheim.cellar.identity.internal.adapter.out.security;

import be.mjodheim.cellar.identity.internal.application.port.UserRepository;
import be.mjodheim.cellar.identity.internal.domain.Role;
import be.mjodheim.cellar.identity.internal.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAccountValidatorTest {

    @Mock UserRepository users;

    @Test
    void acceptsTheCurrentAccountAndRole() {
        when(users.findById(1L)).thenReturn(Optional.of(user()));
        assertFalse(new JwtAccountValidator(users).validate(token("1", 1L, "USER")).hasErrors());
    }

    @Test
    void rejectsOldEmailSubjectsAndInconsistentIds() {
        JwtAccountValidator validator = new JwtAccountValidator(users);
        assertTrue(validator.validate(token("user@example.com", 1L, "USER")).hasErrors());
        assertTrue(validator.validate(token("1", 2L, "USER")).hasErrors());
        verifyNoInteractions(users);
    }

    @Test
    void rejectsDisabledAccounts() {
        User user = user();
        user.disable(Instant.now());
        when(users.findById(1L)).thenReturn(Optional.of(user));
        assertTrue(new JwtAccountValidator(users).validate(token("1", 1L, "USER")).hasErrors());
    }

    @Test
    void rejectsAnOutdatedAdministratorRole() {
        when(users.findById(1L)).thenReturn(Optional.of(user()));
        assertTrue(new JwtAccountValidator(users).validate(token("1", 1L, "ADMIN")).hasErrors());
    }

    private static User user() {
        Instant now = Instant.now();
        return User.rehydrate(1L, "user@example.com", "User", "hash", Role.USER, true, now, now, null);
    }

    private static Jwt token(String subject, Long uid, String role) {
        return Jwt.withTokenValue("test-token").header("alg", "HS256")
                .subject(subject).claim("uid", uid).claim("roles", List.of(role)).build();
    }
}

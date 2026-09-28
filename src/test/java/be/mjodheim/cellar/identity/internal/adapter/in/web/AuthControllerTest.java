package be.mjodheim.cellar.identity.internal.adapter.in.web;

import be.mjodheim.cellar.identity.internal.application.*;
import be.mjodheim.cellar.identity.internal.domain.Role;
import be.mjodheim.cellar.identity.internal.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock AuthenticationService authenticationService;
    @Mock UserAccountService userAccountService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AuthController(authenticationService, userAccountService))
                .build();
    }

    @Test
    void shouldRegisterUser() throws Exception {
        when(authenticationService.register(
                "user@example.com",
                "User",
                "very-secure-password"
        )).thenReturn(result());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "user@example.com",
                                  "displayName": "User",
                                  "password": "very-secure-password",
                                  "passwordConfirm": "very-secure-password"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").value("access"))
                .andExpect(jsonPath("$.refreshToken").value("refresh"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void shouldRejectMismatchedPasswords() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "user@example.com",
                                  "displayName": "User",
                                  "password": "very-secure-password",
                                  "passwordConfirm": "different-password"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authenticationService);
    }

    @Test
    void shouldReturn401ForInvalidCredentials() throws Exception {
        when(authenticationService.login(anyString(), anyString()))
                .thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "user@example.com",
                                  "password": "wrong-password"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Authentication failed"));
    }

    @Test
    void shouldReturnCurrentProfile() throws Exception {
        Instant now = Instant.parse("2026-09-28T12:00:00Z");
        User user = User.rehydrate(
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

        when(userAccountService.findCurrent("user@example.com")).thenReturn(user);

        mockMvc.perform(get("/api/auth/me")
                        .principal(new UsernamePasswordAuthenticationToken(
                                "user@example.com",
                                null
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.displayName").value("User"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    private static AuthenticationResult result() {
        return new AuthenticationResult(
                "access",
                "refresh",
                900,
                1L,
                "user@example.com",
                "User",
                Role.USER
        );
    }
}

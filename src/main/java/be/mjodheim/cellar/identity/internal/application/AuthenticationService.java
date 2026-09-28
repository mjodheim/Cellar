package be.mjodheim.cellar.identity.internal.application;

import be.mjodheim.cellar.identity.internal.application.port.AccessTokenPort;
import be.mjodheim.cellar.identity.internal.application.port.PasswordHashingPort;
import be.mjodheim.cellar.identity.internal.application.port.RefreshTokenCodec;
import be.mjodheim.cellar.identity.internal.application.port.RefreshTokenRepository;
import be.mjodheim.cellar.identity.internal.application.port.UserRepository;
import be.mjodheim.cellar.identity.internal.domain.RefreshToken;
import be.mjodheim.cellar.identity.internal.domain.Role;
import be.mjodheim.cellar.identity.internal.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

/**
 * Coordinates registration, login, refresh-token rotation and logout.
 *
 * <p>Passwords are hashed through an outbound port before entering the domain.
 * Refresh tokens are opaque, rotated on every refresh and persisted only as hashes.</p>
 */
@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordHashingPort passwordHashingPort;
    private final AccessTokenPort accessTokenPort;
    private final RefreshTokenCodec refreshTokenCodec;
    private final Duration refreshTokenTtl;

    public AuthenticationService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordHashingPort passwordHashingPort,
            AccessTokenPort accessTokenPort,
            RefreshTokenCodec refreshTokenCodec,
            @Value("${cellar.security.jwt.refresh-token-ttl}") Duration refreshTokenTtl
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordHashingPort = passwordHashingPort;
        this.accessTokenPort = accessTokenPort;
        this.refreshTokenCodec = refreshTokenCodec;
        this.refreshTokenTtl = refreshTokenTtl;
    }

    /** Registers a regular USER account and returns its first token pair. */
    @Transactional
    public AuthenticationResult register(String email, String displayName, String rawPassword) {
        String normalizedEmail = normalizeEmail(email);
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyRegisteredException(normalizedEmail);
        }

        Instant now = Instant.now();
        User user = User.register(
                normalizedEmail,
                displayName,
                passwordHashingPort.hash(rawPassword),
                Role.USER,
                now
        );

        return issueSession(userRepository.save(user), now);
    }

    /** Authenticates an enabled, non-deleted account. */
    @Transactional
    public AuthenticationResult login(String email, String rawPassword) {
        User user = userRepository.findByEmail(normalizeEmail(email))
                .filter(candidate -> candidate.enabled() && !candidate.isDeleted())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordHashingPort.matches(rawPassword, user.passwordHash())) {
            throw new InvalidCredentialsException();
        }

        return issueSession(user, Instant.now());
    }

    /** Rotates a valid refresh token and returns a fresh token pair. */
    @Transactional
    public AuthenticationResult refresh(String rawRefreshToken) {
        Instant now = Instant.now();
        String hash = refreshTokenCodec.hash(rawRefreshToken);

        RefreshToken current = refreshTokenRepository.findByTokenHash(hash)
                .filter(token -> token.isActiveAt(now))
                .orElseThrow(InvalidRefreshTokenException::new);

        User user = userRepository.findById(current.userId())
                .filter(candidate -> candidate.enabled() && !candidate.isDeleted())
                .orElseThrow(InvalidRefreshTokenException::new);

        current.revoke(now);
        refreshTokenRepository.save(current);

        return issueSession(user, now);
    }

    /** Revokes the supplied refresh token if present. Logout is intentionally idempotent. */
    @Transactional
    public void logout(String rawRefreshToken) {
        String hash = refreshTokenCodec.hash(rawRefreshToken);
        refreshTokenRepository.findByTokenHash(hash).ifPresent(token -> {
            token.revoke(Instant.now());
            refreshTokenRepository.save(token);
        });
    }

    private static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private AuthenticationResult issueSession(User user, Instant now) {
        RefreshTokenCodec.GeneratedRefreshToken generated = refreshTokenCodec.generate();
        RefreshToken refresh = RefreshToken.issue(
                user.id(),
                generated.tokenHash(),
                now.plus(refreshTokenTtl),
                now
        );
        refreshTokenRepository.save(refresh);

        return new AuthenticationResult(
                accessTokenPort.generate(user),
                generated.rawToken(),
                accessTokenPort.expiresInSeconds(),
                user.id(),
                user.email(),
                user.displayName(),
                user.role()
        );
    }
}

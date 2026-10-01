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

    /**
     * Creates the authentication use-case service.
     *
     * @param userRepository user persistence port
     * @param refreshTokenRepository refresh-token persistence port
     * @param passwordHashingPort password hashing boundary
     * @param accessTokenPort access-token issuing boundary
     * @param refreshTokenCodec refresh-token generation and hashing boundary
     * @param refreshTokenTtl configured refresh-token lifetime
     */
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

    /**
     * Registers a regular USER account and returns its first token pair.
     *
     * @param email requested email
     * @param displayName user-facing display name
     * @param rawPassword clear-text password supplied for this request only
     * @return authentication result containing the first access and refresh tokens
     * @throws EmailAlreadyRegisteredException when the email is already used
     */
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

    /**
     * Authenticates an enabled, non-deleted account.
     *
     * @param email account email
     * @param rawPassword submitted clear-text password
     * @return fresh authentication result
     * @throws InvalidCredentialsException when authentication fails
     */
    @Transactional
    public AuthenticationResult login(String email, String rawPassword) {
        User user = userRepository.findByEmailForUpdate(normalizeEmail(email))
                .filter(candidate -> candidate.enabled() && !candidate.isDeleted())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordHashingPort.matches(rawPassword, user.passwordHash())) {
            throw new InvalidCredentialsException();
        }

        return issueSession(user, Instant.now());
    }

    /**
     * Rotates a valid refresh token and returns a fresh token pair.
     *
     * @param rawRefreshToken raw opaque refresh token
     * @return refreshed authentication result
     * @throws InvalidRefreshTokenException when the token is unknown, expired, revoked or belongs to an unavailable user
     */
    @Transactional
    public AuthenticationResult refresh(String rawRefreshToken) {
        String hash = refreshTokenCodec.hash(rawRefreshToken);
        Long userId = refreshTokenRepository.findUserIdByTokenHash(hash)
                .orElseThrow(InvalidRefreshTokenException::new);

        // Account locks always precede token locks, also during account deletion.
        User user = userRepository.findByIdForUpdate(userId)
                .filter(candidate -> candidate.enabled() && !candidate.isDeleted())
                .orElseThrow(InvalidRefreshTokenException::new);
        RefreshToken current = refreshTokenRepository.findByTokenHashForUpdate(hash)
                .orElseThrow(InvalidRefreshTokenException::new);
        Instant now = Instant.now();
        if (!current.userId().equals(user.id()) || !current.isActiveAt(now)) {
            throw new InvalidRefreshTokenException();
        }

        current.revoke(now);
        refreshTokenRepository.save(current);

        return issueSession(user, now);
    }

    /**
     * Revokes the supplied refresh token if present.
     *
     * <p>Logout is intentionally idempotent.</p>
     *
     * @param rawRefreshToken token to revoke
     */
    @Transactional
    public void logout(String rawRefreshToken) {
        String hash = refreshTokenCodec.hash(rawRefreshToken);
        refreshTokenRepository.findByTokenHashForUpdate(hash).ifPresent(token -> {
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

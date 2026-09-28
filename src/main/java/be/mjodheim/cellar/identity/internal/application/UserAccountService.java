package be.mjodheim.cellar.identity.internal.application;

import be.mjodheim.cellar.identity.internal.application.port.RefreshTokenRepository;
import be.mjodheim.cellar.identity.internal.application.port.UserRepository;
import be.mjodheim.cellar.identity.internal.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Handles lifecycle operations for the authenticated account.
 */
@Service
public class UserAccountService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserAccountService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /** Returns the current non-deleted account. */
    @Transactional(readOnly = true)
    public User findCurrent(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    /** Soft-deletes the account and revokes all still-active refresh tokens. */
    @Transactional
    public void deleteCurrent(String email) {
        User user = findCurrent(email);
        Instant now = Instant.now();

        user.softDelete(now);
        userRepository.save(user);

        for (var token : refreshTokenRepository.findActiveByUserId(user.id())) {
            token.revoke(now);
            refreshTokenRepository.save(token);
        }
    }
}

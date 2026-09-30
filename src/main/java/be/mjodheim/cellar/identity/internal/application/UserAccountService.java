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

    /**
     * Creates the account service.
     *
     * @param userRepository user persistence port
     * @param refreshTokenRepository refresh-token persistence port
     */
    public UserAccountService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * Returns the current non-deleted account.
     *
     * @param userId immutable authenticated account identifier
     * @return matching user account
     * @throws UserNotFoundException when the account no longer exists
     */
    @Transactional(readOnly = true)
    public User findCurrent(Long userId) {
        return userRepository.findById(userId)
                .filter(user -> user.enabled() && !user.isDeleted())
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    /**
     * Soft-deletes the account and revokes all still-active refresh tokens.
     *
     * @param userId immutable authenticated account identifier
     */
    @Transactional
    public void deleteCurrent(Long userId) {
        User user = userRepository.findByIdForUpdate(userId)
                .filter(candidate -> candidate.enabled() && !candidate.isDeleted())
                .orElseThrow(() -> new UserNotFoundException(userId));
        Instant now = Instant.now();

        user.softDelete(now);
        userRepository.save(user);

        for (var token : refreshTokenRepository.findActiveByUserId(user.id())) {
            token.revoke(now);
            refreshTokenRepository.save(token);
        }
    }
}

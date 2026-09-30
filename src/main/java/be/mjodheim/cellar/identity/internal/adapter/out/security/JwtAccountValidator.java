package be.mjodheim.cellar.identity.internal.adapter.out.security;

import be.mjodheim.cellar.identity.internal.application.port.UserRepository;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Binds signed tokens to immutable, enabled accounts. Email reuse cannot
 * transfer a token; deletion, disabling and role changes invalidate it.
 */
final class JwtAccountValidator implements OAuth2TokenValidator<Jwt> {

    private static final OAuth2Error INVALID_ACCOUNT =
            new OAuth2Error("invalid_token", "The token does not identify an active account", null);
    private final UserRepository userRepository;

    JwtAccountValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        try {
            long userId = Long.parseLong(token.getSubject());
            Object claimedId = token.getClaim("uid");
            if (userId <= 0 || claimedId == null || Long.parseLong(claimedId.toString()) != userId) {
                return OAuth2TokenValidatorResult.failure(INVALID_ACCOUNT);
            }
            boolean valid = userRepository.findById(userId)
                    .filter(user -> user.enabled() && !user.isDeleted())
                    .filter(user -> java.util.List.of(user.role().name())
                            .equals(token.getClaimAsStringList("roles")))
                    .isPresent();
            return valid ? OAuth2TokenValidatorResult.success()
                    : OAuth2TokenValidatorResult.failure(INVALID_ACCOUNT);
        } catch (IllegalArgumentException | NullPointerException exception) {
            return OAuth2TokenValidatorResult.failure(INVALID_ACCOUNT);
        }
    }
}

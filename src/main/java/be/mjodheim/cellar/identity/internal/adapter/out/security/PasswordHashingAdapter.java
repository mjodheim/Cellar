package be.mjodheim.cellar.identity.internal.adapter.out.security;

import be.mjodheim.cellar.identity.internal.application.port.PasswordHashingPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Spring Security implementation of the password-hashing boundary.
 */
@Component
class PasswordHashingAdapter implements PasswordHashingPort {

    private final PasswordEncoder passwordEncoder;

    PasswordHashingAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /** {@inheritDoc} */
    @Override
    public String hash(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /** {@inheritDoc} */
    @Override
    public boolean matches(String rawPassword, String passwordHash) {
        return passwordEncoder.matches(rawPassword, passwordHash);
    }
}

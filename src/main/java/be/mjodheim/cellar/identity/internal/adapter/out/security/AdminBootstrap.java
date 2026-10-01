package be.mjodheim.cellar.identity.internal.adapter.out.security;

import be.mjodheim.cellar.identity.internal.application.port.PasswordHashingPort;
import be.mjodheim.cellar.identity.internal.application.port.UserRepository;
import be.mjodheim.cellar.identity.internal.domain.Role;
import be.mjodheim.cellar.identity.internal.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Optional one-time administrator bootstrap.
 *
 * <p>If all three {@code BOOTSTRAP_ADMIN_*} variables are provided and the email
 * does not already exist, an ADMIN account is created. No bootstrap credential is
 * stored in source control.</p>
 */
@Component
class AdminBootstrap implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordHashingPort passwordHashingPort;
    private final String email;
    private final String displayName;
    private final String password;

    /**
     * Creates the bootstrap component from repositories and environment-backed properties.
     */
    AdminBootstrap(
            UserRepository userRepository,
            PasswordHashingPort passwordHashingPort,
            @Value("${cellar.security.bootstrap-admin.email:}") String email,
            @Value("${cellar.security.bootstrap-admin.display-name:}") String displayName,
            @Value("${cellar.security.bootstrap-admin.password:}") String password
    ) {
        this.userRepository = userRepository;
        this.passwordHashingPort = passwordHashingPort;
        this.email = email;
        this.displayName = displayName;
        this.password = password;
    }

    /**
     * Creates the initial administrator only when all bootstrap settings are present
     * and the configured email does not already exist.
     *
     * @param args Spring Boot application arguments
     */
    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (email.isBlank() || displayName.isBlank() || password.isBlank()) {
            return;
        }
        if (userRepository.existsByEmail(email)) {
            return;
        }

        Instant now = Instant.now();
        userRepository.save(User.register(
                email,
                displayName,
                passwordHashingPort.hash(password),
                Role.ADMIN,
                now
        ));
    }
}

package be.mjodheim.cellar;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.security.SecureRandom;
import java.util.Base64;

/** A disposable database shared by the integration tests; local .env is never read. */
@SpringBootTest(properties = {
        "cellar.security.bootstrap-admin.email=",
        "cellar.security.bootstrap-admin.display-name=",
        "cellar.security.bootstrap-admin.password="
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class PostgresIntegrationSupport {

    // Singleton lifecycle keeps the cached Spring context connected between classes.
    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:18-alpine")
            .withDatabaseName("cellar_tests")
            .withUsername("cellar_tests")
            .withPassword("temporary-test-password");
    private static final String JWT_SECRET = generateSecret();

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        synchronized (POSTGRES) {
            if (!POSTGRES.isRunning()) {
                POSTGRES.start();
            }
        }
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("cellar.security.jwt.secret", () -> JWT_SECRET);
    }

    private static String generateSecret() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}

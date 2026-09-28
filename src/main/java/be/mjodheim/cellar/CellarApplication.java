package be.mjodheim.cellar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point of the Cellar Spring Boot application.
 *
 * <p>The root package is also used by Spring Boot and Spring Modulith to discover
 * application components and business modules.</p>
 */
@SpringBootApplication
public class CellarApplication {

    /**
     * Starts Cellar with the standard Spring Boot bootstrap process.
     *
     * @param args command-line arguments forwarded to Spring Boot
     */
    public static void main(String[] args) {
        SpringApplication.run(CellarApplication.class, args);
    }

}

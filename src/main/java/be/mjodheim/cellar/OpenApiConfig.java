package be.mjodheim.cellar;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Central OpenAPI metadata and reusable Bearer JWT authentication scheme.
 */
@Configuration
class OpenApiConfig {

    /**
     * Builds the OpenAPI document exposed by Springdoc.
     *
     * @return configured OpenAPI model including the global Bearer security scheme
     */
    @Bean
    OpenAPI cellarOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cellar API")
                        .version("1.0.0")
                        .description("API de gestion du catalogue, du stock, des commandes et des identités Mjödheim"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes(
                                "bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Jeton d'accès JWT retourné par /api/auth/login")
                        ));
    }
}

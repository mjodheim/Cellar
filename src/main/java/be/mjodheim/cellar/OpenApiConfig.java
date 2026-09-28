package be.mjodheim.cellar;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OpenApiConfig {

    @Bean
    OpenAPI cellarOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cellar API")
                        .version("1.0.0")
                        .description("API de gestion du catalogue, du stock et des commandes Mjödheim"));
    }
}

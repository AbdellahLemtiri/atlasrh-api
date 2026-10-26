package com.atlasrh.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI atlasRhOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AtlasRH API")
                        .description("Documentation complète de l'API REST de gestion des ressources humaines")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Abdellah")
                                .email("abdellah@test.com")));
    }
}
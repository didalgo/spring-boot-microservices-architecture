package com.idalgo.daniel.notificationservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI configuration for Notification Service.
 *
 * Provides Swagger UI and OpenAPI specification.
 * Access Swagger UI at: http://localhost:8083/swagger-ui.html
 * Access OpenAPI spec at: http://localhost:8083/v3/api-docs
 */
@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Notification Service API")
                        .version("1.0.0")
                        .description("REST API for managing notifications in the e-commerce microservices system")
                        .contact(new Contact()
                                .name("Daniel Idalgo")
                                .email("contact@example.com")
                        )
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")
                        )
                )
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8083")
                                .description("Development server"),
                        new Server()
                                .url("http://localhost:8080/api/notifications")
                                .description("API Gateway")
                ));
    }
}
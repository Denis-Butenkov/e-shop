package com.lumastyle.eshop.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // Define JWT Bearer Security Scheme
        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        // Apply Security Requirement globally
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("bearerAuth");

        return new OpenAPI()
                // Server configuration for HTTPS
                .addServersItem(new Server().url("https://localhost:8443"))
                // Register security scheme
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", bearerAuth)
                )
                // Apply security requirement
                .addSecurityItem(securityRequirement)
                // API metadata
                .info(new Info()
                        .title("E-commerce API")
                        .version("v1")
                        .description("REST API for e-commerce – Spring Boot + MongoDB"));
    }
}

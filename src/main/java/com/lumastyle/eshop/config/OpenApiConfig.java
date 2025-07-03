package com.lumastyle.eshop.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.servers.ServerVariable;
import io.swagger.v3.oas.models.servers.ServerVariables;
import io.swagger.v3.oas.models.tags.Tag;
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

        // Server with variable BASE_URL
        ServerVariable baseUrlVar = new ServerVariable()
                ._default("https://localhost:8443")
                .description("Base URL for the API");
        ServerVariables vars = new ServerVariables();
        vars.addServerVariable("baseUrl", baseUrlVar);

        Server prodServer = new Server()
                .url("{baseUrl}")
                .description("Dynamic environment")
                .variables(vars);

        return new OpenAPI()
                // Server configuration
                .addServersItem(prodServer)
                .addServersItem(new Server()
                        .url("https://api.example.com")
                        .description("Production API server"))

                // Global components including a security scheme
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", bearerAuth)
                )
                .addSecurityItem(securityRequirement)

                // Tags grouping for clarity
                .addTagsItem(new Tag().name("Auth").description("Authentication operations"))
                .addTagsItem(new Tag().name("Products").description("Product management"))
                .addTagsItem(new Tag().name("Cart").description("Shopping cart operations"))
                .addTagsItem(new Tag().name("Orders").description("Order operations"))
                .addTagsItem(new Tag().name("Users").description("Registration operations"))

                // External docs linking
                .externalDocs(new ExternalDocumentation()
                        .description("GitHub repository")
                        .url("https://github.com/NightmareFDD/e-shop"))

                // API metadata
                .info(new Info()
                        .title("E-commerce API")
                        .version("v1.0.0")
                        .description("Complete REST API for e-commerce – Spring Boot")
                );
    }
}

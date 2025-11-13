package com.vetflow.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Central OpenAPI/Swagger configuration.
 */
@Configuration
public class OpenApiConfig {

  private static final String SECURITY_SCHEME_NAME = "bearerAuth";

  @Bean
  public OpenAPI vetflowOpenApi() {
    return new OpenAPI()
        .info(new Info()
            .title("VetFlow API")
            .description("REST interface for appointments, patients, medical records and auth")
            .version("v1")
            .contact(new Contact().name("Mauricio Pasten (mrp4sten)").email("mauricio.pasten.martinez@gmail.com"))
            .license(new License().name("Apache 2.0")))
        .components(new Components()
            .addSecuritySchemes(SECURITY_SCHEME_NAME, jwtSecurityScheme()))
        .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
  }

  private SecurityScheme jwtSecurityScheme() {
    return new SecurityScheme()
        .name("Authorization")
        .type(SecurityScheme.Type.HTTP)
        .scheme("bearer")
        .bearerFormat("JWT")
        .description("Supply the JWT access token (without the Bearer prefix for curl, with Bearer for Swagger UI).");
  }
}

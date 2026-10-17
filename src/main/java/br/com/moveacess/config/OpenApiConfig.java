package br.com.moveacess.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.*;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info().title("MoveAcess API").version("0.0.1")
                .description("Mobilidade urbana acessível para pessoas cadeirantes"))
            .addSecurityItem(new SecurityRequirement().addList("bearer"))
            .components(new Components().addSecuritySchemes("bearer",
                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
    }
}

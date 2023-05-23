package com.firstproject.springredditclone.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfiguration {


    @Bean
    public OpenAPI expenseAPI() {
        return new OpenAPI().schemaRequirement("jwt-auth",new SecurityScheme().scheme("Bearer").type(SecurityScheme.Type.HTTP).in(SecurityScheme.In.HEADER))
                .info(new Info().title("Reddit Clone API")
                        .description("API for Reddit Clone Project")
                        .version("v0.0.1"));
    }
}
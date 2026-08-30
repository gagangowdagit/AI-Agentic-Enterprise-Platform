package com.rag.ragbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ragBackendOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("RAG Backend API")
                .description("AI Agentic Enterprise Platform backend API")
                .version("v1.0.0"));
    }
}

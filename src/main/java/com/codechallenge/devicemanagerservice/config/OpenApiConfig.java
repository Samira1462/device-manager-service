package com.codechallenge.devicemanagerservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI deviceManagerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Device Manager API")
                        .description("REST API for managing devices")
                        .version("1.0.0"));
    }
}
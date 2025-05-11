package com.example.chatapp.infrastructure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.OpenAPI

@Configuration
class SwaggerConfig {
    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Chat System API")
                    .version("1.0.0")
                    .description("This is a Chat System API documented with Swagger and SpringDoc.")
            )
    }
}
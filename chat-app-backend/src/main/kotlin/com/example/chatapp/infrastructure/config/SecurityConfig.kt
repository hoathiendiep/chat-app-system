package com.example.chatapp.infrastructure.config

import com.example.chatapp.infrastructure.security.KeycloakJwtAuthenticationConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() } // Disable CSRF for APIs
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) } // Stateless for JWT
            .authorizeHttpRequests { req ->
                req
                    .requestMatchers(
                        "/actuator2/health",
                        "/auth/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/webjars/**",
                        "/ws/**",
                        "/api/webhook"
                    ).permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2ResourceServer { auth ->
                auth.jwt { token ->
                    token.jwtAuthenticationConverter(KeycloakJwtAuthenticationConverter())
                }
            }
        return http.build()
    }

    @Bean
    fun corsFilter(): CorsFilter {
        return CorsFilter(corsConfigurationSource())
    }

    private fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration().apply {
            allowCredentials = true
            allowedOrigins = listOf("http://localhost:4200")
            allowedHeaders = listOf(
                HttpHeaders.ORIGIN,
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.ACCEPT,
                HttpHeaders.AUTHORIZATION
            )
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH")
        }
        source.registerCorsConfiguration("/**", config)
        return source
    }
}
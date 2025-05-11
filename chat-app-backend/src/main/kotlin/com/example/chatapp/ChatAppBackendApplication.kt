package com.example.chatapp

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.OAuthFlow
import io.swagger.v3.oas.annotations.security.OAuthFlows
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableJpaAuditing(dateTimeProviderRef = "offsetDateTimeProvider")
@EnableJpaRepositories
@EnableScheduling
@ConfigurationPropertiesScan(
	basePackages = [
		"com.example.chatapp"
	]
)
@SecurityScheme(
	name = "keycloak",
	type = SecuritySchemeType.OAUTH2,
	bearerFormat = "JWT",
	scheme = "bearer",
	`in` = SecuritySchemeIn.HEADER,
	flows = OAuthFlows(
		password = OAuthFlow(
			authorizationUrl = "http://localhost:8080/realms/chatapp-realm/protocol/openid-connect/auth",
			tokenUrl = "http://localhost:8080/realms/chatapp-realm/protocol/openid-connect/token"
		)
	)
)
class ChatAppBackendApplication
fun main(args: Array<String>) {
	runApplication<ChatAppBackendApplication>(*args)
}

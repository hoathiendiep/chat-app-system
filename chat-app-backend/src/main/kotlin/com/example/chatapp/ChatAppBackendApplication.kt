package com.example.chatapp

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
class ChatAppBackendApplication
fun main(args: Array<String>) {
	runApplication<ChatAppBackendApplication>(*args)
}

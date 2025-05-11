package com.example.chatapp.infrastructure.config

import com.azure.storage.blob.BlobServiceClient
import com.azure.storage.blob.BlobServiceClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AzureConfiguration(
    @Value("\${spring.cloud.azure.storage.blob.connection-string}")
    private val connectionString: String
) {

    @Bean
    fun connectBlobStorageService(): BlobServiceClient {
        return BlobServiceClientBuilder()
            .connectionString(connectionString).buildClient()
    }


}
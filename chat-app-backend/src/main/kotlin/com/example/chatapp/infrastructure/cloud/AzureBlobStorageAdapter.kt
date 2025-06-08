package com.example.chatapp.infrastructure.cloud

import com.azure.storage.blob.BlobServiceClient
import com.example.chatapp.app.port.AttachmentStoragePort
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AzureBlobStorageAdapter (
    private val blobServiceClient: BlobServiceClient
): AttachmentStoragePort {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun uploadAttachment(containerName: String, path: String, data: ByteArray): String {
        var blobContainerClient = blobServiceClient.getBlobContainerClient(containerName)
        if (!blobContainerClient.exists()) {
            blobContainerClient = blobServiceClient.createBlobContainer(containerName)
            logger.info("Created new container: $containerName")
        }

        val blobClient = blobContainerClient.getBlobClient(path)
        logger.info("Start uploadFile containerName: $containerName path: $path")
        try {
            blobClient.upload(data.inputStream(), true)
        } catch (ex: Exception) {
            logger.error("Error uploadFile containerName: $containerName path: $path due to ${ex.message}")
            ex.printStackTrace()
            throw ex
        }
        return blobClient.blobUrl
    }

    override fun getData(containerName: String?,path: String?): ByteArray? {
        if (StringUtils.isBlank(path)){
            return null
        }
        logger.info("Start getData containerName: $containerName path: $path")
        val blobContainerClient = blobServiceClient.getBlobContainerClient(containerName)
        val blobClient = blobContainerClient.getBlobClient(path)
        try {
            return blobClient.downloadContent().toBytes()
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("Error getData containerName: $containerName path: $path due to ${e.message}")
            throw e
        }

    }
}
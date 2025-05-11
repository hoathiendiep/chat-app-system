package com.example.chatapp.app.port

interface AttachmentStoragePort {
    fun uploadAttachment(containerName: String, path: String, data: ByteArray): String
    fun getData(containerName: String?,path: String?): ByteArray?
}
package com.example.chatapp.domain.message.service

import com.example.chatapp.domain.message.dto.request.MessageRequest
import com.example.chatapp.domain.message.dto.response.AttachmentInfo
import com.example.chatapp.domain.message.dto.response.MessageResponse
import org.springframework.security.core.Authentication
import java.util.*

interface MessageService {
    fun findChatMessages(chatId: UUID, page: Int, size: Int) : List<MessageResponse>
    fun setMessagesToSeen(chatId: UUID, authentication: Authentication)
    fun saveMessage(messageRequest: MessageRequest)
    fun uploadMediaMessage(chatId: UUID, filePath : String, file: ByteArray, authentication: Authentication) : UUID
    fun getAttachmentByMessageId(messageId: UUID) : AttachmentInfo
}
package com.example.chatapp.domain.chat.service

import com.example.chatapp.domain.chat.dto.response.ChatPreviewResponse
import org.springframework.security.core.Authentication
import java.util.*

interface ChatService {
    fun retrieveChatsPreview(authentication: Authentication, page: Int, size: Int):List<ChatPreviewResponse>
    fun createChat(senderId: UUID,receiverId: UUID) : UUID?
}
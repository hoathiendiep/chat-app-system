package com.example.chatapp.api.rest.v1

import com.example.chatapp.app.dto.response.ApiResponse
import com.example.chatapp.app.dto.response.PageMetadata
import com.example.chatapp.app.service.ApiResponseFactory
import com.example.chatapp.domain.chat.dto.response.ChatPreviewResponse
import com.example.chatapp.domain.chat.service.ChatService
import com.example.chatapp.domain.common.constant.CommonConstant
import com.example.chatapp.domain.common.constant.ResponseCode
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/chats")
@Tag(name = "Chat")
class ChatController(
    private val chatService : ChatService,
    private val responseFactory: ApiResponseFactory
) {
    @PostMapping
    fun createChat(
        @RequestParam(name = "sender_id") senderId: UUID,
        @RequestParam(name = "receiver_id") receiverId: UUID
    ): ApiResponse<UUID> {
        val chatId: UUID? = chatService.createChat(senderId, receiverId)
        return responseFactory.success(
            code = ResponseCode.CREATED,
            data = chatId
        )
    }

    @GetMapping("/chat-preview")
    fun getChatsByReceiver(authentication: Authentication): ApiResponse<List<ChatPreviewResponse>> {
        val result = chatService.retrieveChatsPreview(authentication)
        return responseFactory.success(
            data = result
        )
    }
}
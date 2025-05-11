package com.example.chatapp.domain.message.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class MessageRequest(
    @field:JsonProperty("content")
    val content: String,
    @field:JsonProperty("sender_id")
    val senderId: UUID,
    @field:JsonProperty("receiver_id")
    val receiverId: UUID,
    @field:JsonProperty("type")
    val type: Int,
    @field:JsonProperty("chat_id")
    val chatId: UUID,
    @field:JsonProperty("chat_name")
    val chatName: String
)

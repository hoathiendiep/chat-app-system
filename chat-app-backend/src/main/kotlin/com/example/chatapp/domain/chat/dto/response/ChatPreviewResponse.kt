package com.example.chatapp.domain.chat.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant
import java.time.OffsetDateTime
import java.util.UUID

data class ChatPreviewResponse(
    @field:JsonProperty("id")
    val id: UUID?,
    @field:JsonProperty("chat_name")
    val chatName: String?,
    @field:JsonProperty("sender_id")
    val senderId: UUID?,
    @field:JsonProperty("recipient_id")
    val recipientId: UUID?,
    @field:JsonProperty("unread_count")
    val unreadCount: Long?,
    @field:JsonProperty("last_message")
    val lastMessage: String?,
    @field:JsonProperty("last_message_type")
    val lastMessageType: Int?,
    @field:JsonProperty("last_message_time")
    val lastMessageTime: Instant?,
    @field:JsonProperty("is_recipient_online")
    val recipientOnline: Boolean?

)

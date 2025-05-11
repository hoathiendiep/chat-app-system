package com.example.chatapp.domain.notification.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class Notification(
    @field:JsonProperty("chat_id")
    var chatId: UUID? = null,
    @field:JsonProperty("content")
    val content: String? = null,
    @field:JsonProperty("sender_id")
    val senderId: UUID? = null,
    @field:JsonProperty("receiver_id")
    val receiverId: UUID? = null,
    @field:JsonProperty("chat_name")
    val chatName: String? = null,
    @field:JsonProperty("message_type")
    val messageType: Int? = null,
    @field:JsonProperty("type")
    val type: Int? = null,
    @field:JsonProperty("media")
    var media: ByteArray? =null,
    @field:JsonProperty("user_id")
    val userId: UUID? = null,
    @field:JsonProperty("user_status")
    val userStatus: Boolean ? =null
    )

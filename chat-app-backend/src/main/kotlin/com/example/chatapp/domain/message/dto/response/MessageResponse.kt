package com.example.chatapp.domain.message.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime
import java.util.UUID

data class MessageResponse(
    @field:JsonProperty("id")
    var id: UUID? = null,
    @field:JsonProperty("content")
    val content: String? = null,
    @field:JsonProperty("type")
    val type: Int? = null,
    @field:JsonProperty("state")
    val state: Int? = null,
    @field:JsonProperty("sender_id")
    val senderId: UUID? = null,
    @field:JsonProperty("receiver_id")
    val receiverId: UUID? = null,
    @field:JsonProperty("created_date")
    val createdDate: OffsetDateTime? = null,
    @field:JsonProperty("attachment_file")
    val attachmentFile: ByteArray? = null,
    @field:JsonProperty("file_name")
    var fileName: String? = null
)

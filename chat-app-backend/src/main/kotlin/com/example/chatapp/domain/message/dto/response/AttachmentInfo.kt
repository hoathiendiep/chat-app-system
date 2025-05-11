package com.example.chatapp.domain.message.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class AttachmentInfo(
    @field:JsonProperty("data")
    val data: ByteArray? = null,
    @field:JsonProperty("file_name")
    val fileName: String? = null
)

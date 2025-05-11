package com.example.chatapp.domain.chat.dto.request

data class ChatListRequest(
    val page: Int = 0,
    val size: Int = 20,
    val sortBy: String = "modifiedDate",
    val sortDirection: String = "desc"
)

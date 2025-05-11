package com.example.chatapp.app.dto.response

data class ApiResponse<T>(
    val status: Int,
    val message: String?,
    val data: T? = null,
    val metadata: Any? = null
)

package com.example.chatapp.app.dto.response

data class PageMetadata(
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Long,
    val sort: String
)

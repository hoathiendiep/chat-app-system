package com.example.chatapp.domain.user.dto.response

import java.time.OffsetDateTime
import java.util.UUID

data class UserResponse(
    var id: UUID? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val lastSeen: OffsetDateTime? = null,
    val isOnline: Boolean? = false
)

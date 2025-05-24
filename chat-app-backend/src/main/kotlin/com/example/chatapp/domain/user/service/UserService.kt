package com.example.chatapp.domain.user.service

import com.example.chatapp.domain.user.dto.response.UserResponse
import org.springframework.security.core.Authentication
import java.util.UUID

interface UserService {
    fun findAllUsersExceptSelf(connectedUser: Authentication): List<UserResponse>
    fun setUserStatus(id: UUID, status: Boolean)

}
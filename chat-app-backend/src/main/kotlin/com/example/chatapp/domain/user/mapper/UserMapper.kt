package com.example.chatapp.domain.user.mapper

import com.example.chatapp.domain.user.dto.response.UserResponse
import com.example.chatapp.domain.user.entity.User
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.util.*

@Component
class UserMapper {
    fun fromTokenAttributes(attributes: Map<String?, Any>): User {
        val user = User()

        if (attributes.containsKey("sub")) {
            user.id = UUID.fromString(attributes["sub"].toString())
        }

        if (attributes.containsKey("given_name")) {
            user.firstName = attributes["given_name"].toString()
        } else if (attributes.containsKey("nickname")) {
            user.firstName = attributes["nickname"].toString()
        }

        if (attributes.containsKey("family_name")) {
            user.lastName = attributes["family_name"].toString()
        }

        if (attributes.containsKey("email")) {
            user.email = attributes["email"].toString()
        }
        user.lastSeen = OffsetDateTime.now()
        return user
    }

    fun toUserResponse(user: User): UserResponse {
        return UserResponse(
            id = user.id,
            firstName = user.firstName,
            lastName = user.lastName,
            email = user.email,
            isOnline = user.isUserOnline()
        )

    }
}
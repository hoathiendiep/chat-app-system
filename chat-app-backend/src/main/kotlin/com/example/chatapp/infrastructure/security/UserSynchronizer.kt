package com.example.chatapp.infrastructure.security

import com.example.chatapp.domain.user.entity.User
import com.example.chatapp.domain.user.mapper.UserMapper
import com.example.chatapp.infrastructure.persistence.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import java.util.*
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class UserSynchronizer (
    private val userRepository: UserRepository,
    private val userMapper: UserMapper
){
    private val log = LoggerFactory.getLogger(this::class.java)
    fun synchronizeWithIdp(token: Jwt) {
        log.info("Synchronizing user with idp")
        getUserEmail(token).ifPresent { userEmail: String? ->
            log.info("Synchronizing user having email {}", userEmail)
            val optUser = userRepository.findByEmail(userEmail)
            val user = userMapper.fromTokenAttributes(token.claims)
            optUser.ifPresent { value: User ->
                user.id = value.id;
            }
            userRepository.save(user)
        }
    }

    private fun getUserEmail(token: Jwt): Optional<String> {
        val attributes = token.claims
        if (attributes.containsKey("email")) {
            return Optional.of(attributes["email"].toString())
        }
        return Optional.empty()
    }
}
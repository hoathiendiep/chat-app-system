package com.example.chatapp.infrastructure.notification.ws

import com.example.chatapp.domain.common.constant.CommonConstant
import com.example.chatapp.domain.user.service.UserService
import com.example.chatapp.infrastructure.tracking.PresenceTracker
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionConnectEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Component
class WebSocketEvents(
    private val presenceTracker: PresenceTracker,
    private val userService: UserService
) {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    @EventListener
    fun handleSessionConnected(event: SessionConnectEvent) {
        val userId = extractUserId(event.message.headers) ?: return
        log.info("User connected with UUID: $userId")
        presenceTracker.markActive(userId)
        userService.setUserStatus(userId, true)
    }

    @EventListener
    fun handleSessionDisconnect(event: SessionDisconnectEvent) {
        val userId = extractUserId(event.message.headers) ?: return
        Executors.newSingleThreadScheduledExecutor().schedule({
            if (presenceTracker.isStillDisconnected(userId, 300)) {
                log.info("User disconnected with UUID: $userId")
                userService.setUserStatus(userId, false)
            }
        }, 5, TimeUnit.SECONDS)
    }

    private fun extractUserId(headers: Map<String, Any>): UUID? {
        val sessionAttributes = headers[CommonConstant.WebSocketConstants.SIMP_SESSION_ATTRIBUTES] as? Map<*, *>
        val userIdStr = sessionAttributes?.get(CommonConstant.WebSocketConstants.USER_ID) as? String ?: return null
        return try {
            UUID.fromString(userIdStr)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}

package com.example.chatapp.infrastructure.notification.ws

import com.example.chatapp.app.port.NotificationSenderPort
import com.example.chatapp.domain.notification.dto.Notification
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class WebSocketNotificationSender (
    private val messagingTemplate: SimpMessagingTemplate
) : NotificationSenderPort {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun send(userId: UUID?, notification: Notification) {
        log.info("Sending WS notification to {} with payload {}", userId, notification)
        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/chat",
            notification
        )
    }
}
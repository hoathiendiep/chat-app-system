package com.example.chatapp.app.port

import com.example.chatapp.domain.notification.dto.Notification
import java.util.UUID

interface NotificationSenderPort {
    fun send(userId: UUID?, notification: Notification)
}
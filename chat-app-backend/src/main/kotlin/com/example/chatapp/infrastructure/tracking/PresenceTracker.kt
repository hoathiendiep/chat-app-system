package com.example.chatapp.infrastructure.tracking

import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Component
class PresenceTracker {
    private val userLastSeen = ConcurrentHashMap<UUID, OffsetDateTime>()


//    fun getInactiveUsers(thresholdMinutes: Long): List<UUID> {
//        val now = OffsetDateTime.now()
//        return userLastSeen.filterValues {
//            it.isBefore(now.minusMinutes(thresholdMinutes))
//        }.keys.toList()
//    }

    fun markActive(userId: UUID) {
        userLastSeen[userId] = OffsetDateTime.now()
    }

    fun removeUser(userId: UUID): Boolean {
        return userLastSeen.remove(userId) != null
    }

    fun isOnline(userId: UUID): Boolean {
        return userLastSeen.containsKey(userId)
    }

    fun getAllOnline(): Set<UUID> = userLastSeen.keys

    fun isStillDisconnected(userId: UUID, thresholdSeconds: Long): Boolean {
        val lastSeen = userLastSeen[userId] ?: return true
        return lastSeen.isBefore(OffsetDateTime.now().minusSeconds(thresholdSeconds))
    }

}

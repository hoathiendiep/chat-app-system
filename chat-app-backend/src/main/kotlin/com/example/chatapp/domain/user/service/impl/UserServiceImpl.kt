package com.example.chatapp.domain.user.service.impl

import com.example.chatapp.domain.notification.constant.NotificationType
import com.example.chatapp.domain.notification.dto.Notification
import com.example.chatapp.domain.user.dto.response.UserResponse
import com.example.chatapp.domain.user.mapper.UserMapper
import com.example.chatapp.domain.user.service.UserService
import com.example.chatapp.infrastructure.tracking.PresenceTracker
import com.example.chatapp.infrastructure.notification.ws.WebSocketNotificationSender
import com.example.chatapp.infrastructure.persistence.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserServiceImpl(
    private val notificationSender: WebSocketNotificationSender,
    private val userRepository: UserRepository,
    private val userMapper: UserMapper,
    private val presenceTracker: PresenceTracker
) : UserService{
    private val log : org.slf4j.Logger = LoggerFactory.getLogger(UserServiceImpl::class.java)

    override fun findAllUsersExceptSelf(connectedUser: Authentication): List<UserResponse> {
        try {
            log.info("Start findAllUsersExceptSelf ${UUID.fromString(connectedUser.name)}")
            return userRepository.findAllUsersExceptSelf( UUID.fromString(connectedUser.name))
                .stream()
                .map(userMapper::toUserResponse)
                .toList()
        }
        catch (e: Exception){
            log.error("Error findAllUsersExceptSelf due to ${e.message}")
            throw e
        }
    }

//    @Scheduled(fixedRate = 30)
//    override fun updateUserStatus() {
//        try {
//            val users = presenceTracker.getInactiveUsers(5)
//
//            if (users.isNotEmpty()) {
//                val inactiveUsers: List<UUID> = userRepository.findUserIdsInactiveForMoreThan(1)
//                sendNotifications(users, inactiveUsers, false)
//            }
//        } catch (e: Exception) {
//            log.error("Error updateUserStatus due to ${e.message}")
//        }
//    }

    override fun setUserStatus(id: UUID, status: Boolean) {
        try {
            if (!status) {
                presenceTracker.removeUser(id)
            }
            val activeUsers = presenceTracker.getAllOnline()
            if (activeUsers.isNotEmpty()) {
                sendNotifications(activeUsers, id, status)
            }
        } catch (e: Exception) {
            log.error("Error setUserStatus user $id with status online = $status due to ${e.message}")
            throw e
        }
    }

    private fun sendNotifications(activeUsers: Set<UUID>, userId: UUID, status: Boolean) {
        activeUsers.forEach { activeUser ->
            log.info("Start sending user status notifications")
            val notification = Notification(
                userId = userId,
                type = NotificationType.USER_STATUS.code,
                userStatus = status
            )
            notificationSender.send( activeUser, notification)
        }
    }
}
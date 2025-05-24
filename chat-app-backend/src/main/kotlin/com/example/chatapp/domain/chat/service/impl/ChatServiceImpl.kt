package com.example.chatapp.domain.chat.service.impl

import com.example.chatapp.domain.chat.dto.response.ChatPreviewResponse
import com.example.chatapp.domain.chat.entity.Chat
import com.example.chatapp.domain.chat.service.ChatService
import com.example.chatapp.domain.common.constant.ResponseCode
import com.example.chatapp.domain.common.exception.NotFoundException
import com.example.chatapp.domain.common.exception.TechnicalException
import com.example.chatapp.domain.message.constant.MessageState
import com.example.chatapp.infrastructure.persistence.ChatRepository
import com.example.chatapp.infrastructure.persistence.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.util.Optional
import java.util.UUID

@Service
class ChatServiceImpl (
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository
):ChatService {
    private val log : Logger = LoggerFactory.getLogger(ChatServiceImpl::class.java)


    override fun createChat(senderId: UUID, receiverId: UUID): UUID? {
        log.info("Start createChat")
        try {
            val existingChat: Optional<Chat> = chatRepository.findChatByReceiverAndSender(senderId, receiverId)
            if (existingChat.isPresent) {
                return existingChat.get().id
            }

            userRepository.findById(senderId)
                .orElseThrow { NotFoundException(ResponseCode.USER_NOT_FOUND, arrayOf(senderId.toString())) }
            userRepository.findById(receiverId)
                .orElseThrow { NotFoundException(ResponseCode.USER_NOT_FOUND, arrayOf(receiverId.toString())) }

            val chat = Chat()
            chat.senderId = senderId
            chat.recipientId = receiverId

            val savedChat: Chat = chatRepository.save(chat)
            return savedChat.id!!
        }
        catch (ex : Exception){
            ex.printStackTrace()
            log.info("Error createChat due to ${ex.message}")
            throw TechnicalException(ResponseCode.INTERNAL_SERVER_ERROR, arrayOf(ex.message?: ""))
        }
    }

    override fun retrieveChatsPreview(authentication: Authentication) :List<ChatPreviewResponse> {
        log.info("Start retrieveChatsPreview")
        try {
            val userId = UUID.fromString(authentication.name)
            return chatRepository.findChatsWithLastMessageAndUnreadCount(userId,MessageState.SENT.code)
        }
        catch (ex : Exception){
            ex.printStackTrace()
            log.info("Error retrieveChatsPreview due to ${ex.message}")
            throw TechnicalException(ResponseCode.INTERNAL_SERVER_ERROR, arrayOf(ex.message?: ""))
        }

    }
}
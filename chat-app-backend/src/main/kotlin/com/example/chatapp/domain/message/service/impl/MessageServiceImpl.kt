package com.example.chatapp.domain.message.service.impl

import com.example.chatapp.domain.chat.entity.Chat
import com.example.chatapp.domain.common.constant.CommonConstant
import com.example.chatapp.domain.common.constant.ResponseCode
import com.example.chatapp.domain.common.exception.NotFoundException
import com.example.chatapp.domain.common.exception.TechnicalException
import com.example.chatapp.domain.message.constant.MessageState
import com.example.chatapp.domain.message.constant.MessageType
import com.example.chatapp.domain.message.dto.request.MessageRequest
import com.example.chatapp.domain.message.dto.response.AttachmentInfo
import com.example.chatapp.domain.message.dto.response.MessageResponse
import com.example.chatapp.domain.message.entity.Message
import com.example.chatapp.domain.message.mapper.MessageMapper
import com.example.chatapp.domain.message.service.MessageService
import com.example.chatapp.domain.notification.constant.NotificationType
import com.example.chatapp.domain.notification.dto.Notification
import com.example.chatapp.infrastructure.cloud.AzureBlobStorageAdapter
import com.example.chatapp.infrastructure.notification.ws.WebSocketNotificationSender
import com.example.chatapp.infrastructure.persistence.ChatRepository
import com.example.chatapp.infrastructure.persistence.MessageRepository
import org.springframework.transaction.annotation.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.util.*

@Service
class MessageServiceImpl(private val messageRepository: MessageRepository,
    private val chatRepository: ChatRepository,
    private val notificationSender: WebSocketNotificationSender,
    private val azureBlobStorageAdapter: AzureBlobStorageAdapter,
    private val messageMapper: MessageMapper) : MessageService {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun findChatMessages(chatId: UUID, page: Int, size: Int): List<MessageResponse> {
        try {
            log.info("Start findChatMessages chatId ${chatId}")
            val pageable: Pageable = PageRequest.of(page, size, Sort.by("created_date").descending())
            return messageMapper.toMessageResponses(messageRepository.findMessagesByChatId(chatId,pageable))
        }
        catch (e : Exception){
            log.info("Error findChatMessages chatId ${chatId} due to ${e.message}")
            throw TechnicalException(ResponseCode.INTERNAL_SERVER_ERROR, arrayOf(e.message?:""))
        }
    }

    @Transactional
    override fun setMessagesToSeen(chatId: UUID, authentication: Authentication) {
        try {
            log.info("Start setMessagesToSeen chatId ${chatId}")
            val chat: Chat = chatRepository.findById(chatId)
                .orElseThrow { NotFoundException(ResponseCode.CHAT_NOT_FOUND, arrayOf(chatId.toString())) }
             messageRepository.setMessagesToSeenByChatId(chatId, MessageState.SEEN.code)
            val recipientId = if(chat.senderId!!.toString() ==authentication.name) {
                  chat.recipientId!!
            }else {
                  chat.senderId!!
            }

            val notification = Notification(
                chatId = chat.id,
                type = NotificationType.SEEN.code,
                receiverId = recipientId,
//                senderId = getSenderId(chat, authentication)
            )

            notificationSender.send(recipientId, notification)
        }
        catch (e : Exception){
            log.info("Error setMessagesToSeen id ${chatId} due to ${e.message}")
            throw TechnicalException(ResponseCode.INTERNAL_SERVER_ERROR, arrayOf(e.message?:""))
        }    }

    override fun saveMessage(messageRequest: MessageRequest) {
        try {
            log.info("Start saveMessage chatId ${messageRequest.chatId}")
            val chat: Chat = chatRepository.findById(messageRequest.chatId)
                .orElseThrow { NotFoundException(ResponseCode.CHAT_NOT_FOUND, arrayOf(messageRequest.chatId.toString())) }
            val message = Message()
            message.content = messageRequest.content
            message.chatId = messageRequest.chatId
            message.senderId = messageRequest.senderId
            message.receiverId = messageRequest.receiverId
            message.type = messageRequest.type
            message.state = MessageState.SENT.code

            messageRepository.save(message)

            val notification = Notification(
                chatId = chat.id,
                messageType = messageRequest.type,
                content = messageRequest.content,
                senderId = messageRequest.senderId,
                receiverId = messageRequest.receiverId,
                type = NotificationType.MESSAGE.code,
                chatName =  messageRequest.chatName
            )
            notificationSender.send(messageRequest.receiverId, notification)
        }
        catch (e : Exception){
            log.info("Error saveMessage chatId ${messageRequest.chatId} due to ${e.message}")
            throw TechnicalException(ResponseCode.INTERNAL_SERVER_ERROR, arrayOf(e.message?:""))
        }
    }

    override fun uploadMediaMessage(chatId: UUID, path : String, file: ByteArray, authentication: Authentication) : UUID{
        try {
            log.info("Start uploadMediaMessage chatId ${chatId}")
            val chat: Chat = chatRepository.findById(chatId)
                .orElseThrow { NotFoundException(ResponseCode.CHAT_NOT_FOUND, arrayOf(chatId.toString())) }
            val senderId = if(chat.senderId!!.toString() == authentication.name) {
                chat.senderId!!
            }else {
                chat.recipientId!!
            }
            val receiverId = if(chat.senderId!!.toString() ==authentication.name) {
                chat.recipientId!!
            }else {
                chat.senderId!!
            }

            val filePath = azureBlobStorageAdapter.uploadAttachment(CommonConstant.CONTAINER_NAME,path,file)
            val message = Message()
            message.receiverId = receiverId
            message.senderId =  senderId
            message.state = MessageState.SENT.code
            message.type = MessageType.ATTACHMENT.code
            message.attachmentUrl = path
            message.chatId = chatId
            val msg = messageRepository.save(message)

            val notification = Notification(
                chatId = chat.id,
                type = NotificationType.ATTACHMENT.code,
                senderId = senderId,
                receiverId = receiverId,
                messageType = MessageType.ATTACHMENT.code,
                media = azureBlobStorageAdapter.getData(CommonConstant.CONTAINER_NAME,path)
            )
            notificationSender.send(receiverId, notification)
            return msg.id!!
        }
        catch (e : Exception){
            log.info("Error uploadMediaMessage chatId ${chatId} due to ${e.message}")
            throw TechnicalException(ResponseCode.INTERNAL_SERVER_ERROR, arrayOf(e.message?:""))
        }
    }

    override fun getAttachmentByMessageId(messageId: UUID) : AttachmentInfo{
        try {
            log.info("Start getAttachmentByMessageId $messageId")
            val msg = this.messageRepository.findById(messageId).orElseThrow { NotFoundException(ResponseCode.MESSAGE_NOT_FOUND, arrayOf(messageId.toString())) }

            val parts = msg.attachmentUrl?.split("/");

            return AttachmentInfo(
                data = azureBlobStorageAdapter.getData(CommonConstant.CONTAINER_NAME,msg.attachmentUrl),
                fileName = parts?.get(parts.size-1)
            )

        }
        catch (e : Exception){
            log.info("Error getAttachmentByMessageId $messageId due to ${e.message}")
            throw TechnicalException(ResponseCode.INTERNAL_SERVER_ERROR, arrayOf(e.message?:""))
        }
    }

}
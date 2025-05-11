package com.example.chatapp.domain.message.mapper

import com.example.chatapp.app.port.AttachmentStoragePort
import com.example.chatapp.domain.common.constant.CommonConstant
import com.example.chatapp.domain.message.dto.response.MessageResponse
import com.example.chatapp.domain.message.entity.Message
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component

@Component
class MessageMapper (
    private val attachmentStoragePort: AttachmentStoragePort
){
    fun toMessageResponse(message: Message): MessageResponse {
        var msg =  MessageResponse(
            id = message.id,
            content = message.content,
            state = message.state,
            type = message.type,
            senderId = message.senderId,
            receiverId = message.receiverId,
            createdDate = message.createdDate,
            attachmentFile = attachmentStoragePort.getData(CommonConstant.CONTAINER_NAME,message.attachmentUrl),

        )
        if (!StringUtils.isBlank(message.attachmentUrl)) {
            val parts = message.attachmentUrl?.split("/")
            msg.fileName = parts?.get(parts.size-1)
        }
         return msg
    }
    fun toMessageResponses(message: List<Message>): List<MessageResponse> {
        return message.map(::toMessageResponse)
    }
}
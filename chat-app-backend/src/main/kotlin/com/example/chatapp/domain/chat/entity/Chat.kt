package com.example.chatapp.domain.chat.entity

import com.example.chatapp.domain.common.entity.BaseAuditingEntity
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "chat")
class Chat : BaseAuditingEntity() {
    @Id
    var id: UUID ? = UUID.randomUUID()

    @Column(name = "sender_id")
    var senderId: UUID? = null

    @Column(name = "recipient_id")
    var recipientId: UUID? = null

//    @OneToMany(mappedBy = "chat", fetch = FetchType.EAGER)
//    @OrderBy("createdDate DESC")
//    val messages: List<Message>? = null

//    @Transient
//    fun getChatName(senderId: String?): String {
//        if (recipient?.id == senderId) {
//            return sender?.firstName + " " + sender?.lastName
//        }
//        return recipient?.firstName + " " + recipient?.lastName
//    }

//    @Transient
//    fun getTargetChatName(senderId: String?): String {
//        if (sender?.id == senderId) {
//            return sender?.firstName + " " + sender?.lastName
//        }
//        return recipient?.firstName + " " + recipient?.lastName
//    }

//    @Transient
//    fun getUnreadMessages(senderId: String?): Long {
//        return messages!!
//            .filter { it.receiverId == senderId }  // Use Kotlin's filter
//            .filter { it.state == MessageState.SENT }  // Use == for value equality
//            .count().toLong()
//    }


//    @get:Transient
//    val lastMessage: String?
//        get() {
//            if (messages != null && !messages!!.isEmpty()) {
//                if (messages?.get(0)?.type !== MessageType.TEXT) {
//                    return "Attachment"
//                }
//                return messages!![0].content
//            }
//            return null // No messages available
//        }

//    @get:Transient
//    val lastMessageTime: LocalDateTime?
//        get() {
//            if (messages != null && !messages!!.isEmpty()) {
//                return messages!![0].createdDate
//            }
//            return null
//        }
}

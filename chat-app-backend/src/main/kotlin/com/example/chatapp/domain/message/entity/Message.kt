package com.example.chatapp.domain.message.entity

import com.example.chatapp.domain.common.entity.BaseAuditingEntity
import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import java.util.UUID


@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "messages")
class Message : BaseAuditingEntity() {

    @Id
    var id: UUID ? = UUID.randomUUID()

    @Column(name = "content",columnDefinition = "TEXT")
    var content: String? = null

    @Column(name = "state")
    var state: Int? = null

    @Column(name = "type")
    var type: Int? = null

    @Column(name = "chat_id", nullable = false)
    var chatId: UUID? = null

    @Column(name = "sender_id", nullable = false)
    var senderId: UUID? = null

    @Column(name = "receiver_id", nullable = false)
    var receiverId: UUID? = null
    @Column(name = "attachment_url",columnDefinition = "TEXT")
    var attachmentUrl: String? = null
}
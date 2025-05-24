package com.example.chatapp.infrastructure.persistence

import com.example.chatapp.domain.chat.dto.response.ChatPreviewResponse
import com.example.chatapp.domain.chat.entity.Chat
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ChatRepository : JpaRepository<Chat, UUID> {
    @Query(value = "SELECT DISTINCT c FROM chat c WHERE c.sender_id = :senderId OR c.recipient_id = :senderId ORDER BY c.created_date DESC",
        nativeQuery = true)
    fun findChatsBySenderId(@Param("senderId") senderId: UUID): List<Chat>

    @Query(value = "SELECT DISTINCT * FROM chat c WHERE (c.sender_id = :senderId AND c.recipient_id = :recipientId) OR" +
            " (c.sender_id = :recipientId AND c.recipient_id = :senderId)"
    , nativeQuery = true)
    fun findChatByReceiverAndSender(
        @Param("senderId") id: UUID,
        @Param("recipientId") recipientId: UUID
    ): Optional<Chat>

    @Query(
        value = """
                    SELECT 
                        c.id AS id,
                       CASE 
                        WHEN c.recipient_id = :userId 
                            THEN CONCAT(u_sender.first_name, ' ', u_sender.last_name)
                        ELSE CONCAT(u_recipient.first_name, ' ', u_recipient.last_name)
                        END AS chatName,
                        c.sender_id AS senderId,
                        c.recipient_id AS recipientId,
                        COALESCE(uc.unread_count, 0) AS unreadCount,
                        lm.content AS lastMessage,
                        lm.type AS lastMessageType,
                        lm.created_date AS lastMessageTime,
                        CASE 
                            WHEN u_recipient.last_seen >= (CURRENT_TIMESTAMP - INTERVAL '5 minutes')
                            THEN true 
                            ELSE false 
                        END AS recipientOnline

                    FROM chat c
                    INNER JOIN (
                        SELECT DISTINCT ON(m1.chat_id) m1.chat_id, m1.content, m1.created_date, m1.type
                        FROM messages m1 ORDER BY m1.chat_id,m1.created_date DESC 
                    ) lm ON c.id = lm.chat_id
                    LEFT JOIN (
                        SELECT chat_id, COUNT(*) AS unread_count
                        FROM messages
                        WHERE receiver_id = :userId 
                        AND state = :unreadState
                        GROUP BY chat_id
                    ) uc ON c.id = uc.chat_id
                    LEFT JOIN users u_sender ON u_sender.id = c.sender_id
                    LEFT JOIN users u_recipient ON u_recipient.id = c.recipient_id
                    WHERE c.sender_id = :userId OR c.recipient_id = :userId ORDER BY lm.created_date DESC 
        """,
        nativeQuery = true
    )
    fun findChatsWithLastMessageAndUnreadCount(
        @Param("userId") userId: UUID,
        @Param("unreadState") unreadState: Int = 0
    ): List<ChatPreviewResponse>


}
package com.example.chatapp.infrastructure.persistence

import com.example.chatapp.domain.message.entity.Message
import com.example.chatapp.domain.message.constant.MessageState
import com.example.chatapp.domain.message.dto.response.MessageResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface MessageRepository : JpaRepository<Message, UUID>{
    @Query(value = "SELECT * FROM messages m WHERE m.chat_id = :chatId", nativeQuery = true)
    fun findMessagesByChatId(@Param("chatId") chatId: UUID,pageable: Pageable): List<Message>

    @Query(value = "UPDATE messages SET state = :newState WHERE chat_id = :chatId", nativeQuery = true)
    @Modifying
    fun setMessagesToSeenByChatId(@Param("chatId") chatId: UUID, @Param("newState") state: Int)
}
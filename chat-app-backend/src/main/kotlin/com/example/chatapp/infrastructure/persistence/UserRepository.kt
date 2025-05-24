package com.example.chatapp.infrastructure.persistence

import com.example.chatapp.domain.user.entity.User
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    @Query(value = "SELECT u FROM User u WHERE u.email = :email" )
    fun findByEmail(@Param("email") userEmail: String?): Optional<User>

    @Query("SELECT * FROM users u WHERE u.id != :id", nativeQuery = true)
    fun findAllUsersExceptSelf(@Param("id") id: UUID): List<User>


//    @Query(value = "UPDATE users SET is_user_online = :status WHERE id = :userId", nativeQuery = true)
//    @Modifying
//    fun setUserStatus(@Param("userId") userId: UUID, @Param("status") status: Boolean)

    @Query("SELECT u.id FROM users u WHERE u.id != :id", nativeQuery = true)
    fun findAllUserIdsExceptSelf(@Param("id") id: UUID): List<UUID>

    @Query("SELECT u.id FROM users u WHERE u.last_seen < :threshold", nativeQuery = true)
    fun findUserIdsInactiveForMoreThan(@Param("threshold") minutes: Int): List<UUID>
}
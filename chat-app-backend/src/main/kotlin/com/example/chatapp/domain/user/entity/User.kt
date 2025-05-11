package com.example.chatapp.domain.user.entity

import com.example.chatapp.domain.common.entity.BaseAuditingEntity
import jakarta.persistence.*
import jakarta.persistence.criteria.Predicate.BooleanOperator
import java.time.OffsetDateTime
import java.util.UUID


@Entity
@Table(name = "users")
class User : BaseAuditingEntity() {
    companion object {
        const val LAST_ACTIVATE_INTERVAL = 5
    }

    @Id
    var id: UUID ? = UUID.randomUUID()
    @Column(name = "first_name")
    var firstName: String? = null
    @Column(name = "last_name")
    var lastName: String? = null
    @Column(name = "email",unique = true)
    var email: String? = null
    @Column(name = "last_seen")
    var lastSeen: OffsetDateTime? = null

    @Transient
    fun isUserOnline(): Boolean {
        return lastSeen != null && lastSeen!!.isAfter(OffsetDateTime.now().minusMinutes(LAST_ACTIVATE_INTERVAL.toLong()))
    }
}
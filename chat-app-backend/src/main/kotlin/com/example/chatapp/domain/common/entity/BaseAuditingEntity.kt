package com.example.chatapp.domain.common.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.OffsetDateTime


@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
class BaseAuditingEntity {

    @CreatedDate
    @Column(name = "created_date", updatable = false, nullable = false)
    var createdDate: OffsetDateTime? = null

    @LastModifiedDate
    @Column(name = "modified_date", updatable = false, nullable = false)
    var modifiedDate: OffsetDateTime? = null
}
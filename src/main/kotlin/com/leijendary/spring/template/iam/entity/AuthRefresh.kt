package com.leijendary.spring.template.iam.entity

import com.leijendary.spring.template.iam.core.projection.UUIDProjection
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToOne
import java.time.OffsetDateTime
import java.util.*

@Entity
class AuthRefresh : UUIDProjection {
    @Id
    @Column(updatable = false)
    override lateinit var id: UUID

    @OneToOne
    lateinit var auth: Auth

    lateinit var accessTokenId: UUID
    lateinit var token: String
    lateinit var expiresAt: OffsetDateTime
}

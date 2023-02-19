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
    override var id: UUID? = null

    @OneToOne
    var auth: Auth? = null

    var accessTokenId: UUID? = null
    var token: String = ""
    var expiresAt: OffsetDateTime? = null
}

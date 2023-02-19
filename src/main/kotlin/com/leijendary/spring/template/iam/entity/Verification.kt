package com.leijendary.spring.template.iam.entity

import com.leijendary.spring.template.iam.core.entity.IdentityEntity
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.OffsetDateTime

@Entity
class Verification : IdentityEntity() {
    var code = ""
    var deviceId = ""
    var field = ""
    var type = ""
    var expiresAt: OffsetDateTime? = null

    @ManyToOne
    @JoinColumn
    var user: User? = null
}

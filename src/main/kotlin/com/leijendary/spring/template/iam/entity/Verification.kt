package com.leijendary.spring.template.iam.entity

import com.leijendary.spring.template.iam.core.entity.IdentityEntity
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.OffsetDateTime

@Entity
class Verification : IdentityEntity() {
    var code = ""
    var expiry: OffsetDateTime? = null
    var deviceId = ""
    var field = ""
    var type = ""

    @ManyToOne
    @JoinColumn
    var user: User? = null
}

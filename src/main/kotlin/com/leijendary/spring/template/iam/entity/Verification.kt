package com.leijendary.spring.template.iam.entity

import com.leijendary.spring.template.iam.core.entity.IdentityEntity
import com.leijendary.spring.template.iam.entity.listener.VerificationListener
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.OffsetDateTime

@Entity
@EntityListeners(VerificationListener::class)
class Verification : IdentityEntity() {
    var code = ""
    var deviceId = ""
    var type = ""
    var field: String? = null
    var value: String? = null
    var expiresAt: OffsetDateTime? = null

    @ManyToOne
    @JoinColumn
    var user: User? = null

    enum class Type(val value: String) {
        REGISTRATION("registration"),
        EMAIL_CHANGE("emailChange"),
        PHONE_CHANGE("phoneChange"),
        PASSWORD_RESET("passwordReset"),
        PASSWORD_NOMINATE("passwordNominate");

        companion object {
            fun from(value: String) = values().first { it.value == value }
        }

        override fun toString() = value
    }
}

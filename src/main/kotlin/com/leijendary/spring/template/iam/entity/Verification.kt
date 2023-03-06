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
        PASSWORD_RESET("passwordReset"),

        // TODO: logic implementation of the following:
        PASSWORD_NOMINATE("passwordNominate"),
        EMAIL_CHANGE("emailChange"),
        PHONE_CHANGE("phoneChange"),


        // TODO: cleanup the following. Remove the unnecessary enums.
        // this one is temp..
        VERIFICATION("verification"),
        PASSWORD_CHANGE_VERIFY("passwordChangeVerify"),
        EMAIL_VERIFY("emailChange"),
        PHONE_VERIFY("phoneVerify");

        override fun toString() = value
    }
}

package com.leijendary.spring.template.iam.entity

import com.leijendary.spring.template.iam.core.entity.IdentityEntity
import com.leijendary.spring.template.iam.core.util.RequestContext.now
import com.leijendary.spring.template.iam.entity.listener.VerificationListener
import jakarta.persistence.*
import jakarta.persistence.EnumType.STRING
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.OffsetDateTime

@Entity
@EntityListeners(AuditingEntityListener::class, VerificationListener::class)
class Verification : IdentityEntity() {
    enum class Type(val value: String) {
        REGISTRATION("registration"),
        EMAIL_CHANGE("emailChange"),
        PHONE_CHANGE("phoneChange"),
        PASSWORD_RESET("passwordReset"),
        PASSWORD_NOMINATE("passwordNominate");
    }

    @ManyToOne
    @JoinColumn
    var user: User? = null

    lateinit var code: String

    @Enumerated(STRING)
    lateinit var type: Type

    @Enumerated(STRING)
    var field: UserCredential.Type? = null

    var value: String? = null
    lateinit var expiresAt: OffsetDateTime

    @CreatedDate
    @Column(updatable = false)
    var createdAt: OffsetDateTime = now
}

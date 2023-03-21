package com.leijendary.spring.template.iam.entity

import com.leijendary.spring.template.iam.core.entity.IdentityEntity
import com.leijendary.spring.template.iam.core.util.RequestContext.now
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.ManyToOne
import org.springframework.data.annotation.CreatedDate
import java.time.OffsetDateTime

@Entity
class UserCredential : IdentityEntity() {
    enum class Type(val value: String) {
        EMAIL("email"),
        PHONE("phone");
    }

    @ManyToOne
    lateinit var user: User

    lateinit var username: String
    var password: String? = null

    @Enumerated(STRING)
    lateinit var type: Type

    @CreatedDate
    var createdAt: OffsetDateTime = now

    var lastUsedAt: OffsetDateTime? = null
}

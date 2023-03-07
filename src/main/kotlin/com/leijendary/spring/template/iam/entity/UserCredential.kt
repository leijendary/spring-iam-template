package com.leijendary.spring.template.iam.entity

import com.leijendary.spring.template.iam.core.entity.IdentityEntity
import com.leijendary.spring.template.iam.core.util.RequestContext.now
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import org.springframework.data.annotation.CreatedDate
import java.time.OffsetDateTime

@Entity
class UserCredential : IdentityEntity() {
    @ManyToOne
    var user: User? = null

    var username = ""
    var password: String? = null
    var type = ""

    @CreatedDate
    var createdAt: OffsetDateTime = now

    var lastUsedAt: OffsetDateTime? = null

    enum class Type(val value: String) {
        EMAIL("email"),
        PHONE("phone");

        companion object {
            fun from(value: String) = values().first { it.value == value }
        }

        override fun toString() = value
    }
}

package com.leijendary.spring.template.iam.entity

import com.leijendary.spring.template.iam.core.entity.IdentityEntity
import com.leijendary.spring.template.iam.core.util.RequestContext.now
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.ManyToOne
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.OffsetDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
class UserProvider : IdentityEntity() {
    @ManyToOne
    lateinit var user: User

    lateinit var provider: String
    lateinit var type: String
    lateinit var reference: String

    @CreatedDate
    var createdAt: OffsetDateTime = now
}

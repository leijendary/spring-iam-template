package com.leijendary.spring.template.iam.entity

import com.leijendary.spring.template.iam.core.entity.AppEntity
import com.leijendary.spring.template.iam.core.util.RequestContext.now
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import org.springframework.data.annotation.CreatedDate
import java.time.OffsetDateTime

@Entity
class UserSocial : AppEntity() {
    @Id
    var id: String = ""

    @ManyToOne
    var user: User? = null

    var provider: String = ""

    @CreatedDate
    var createdAt: OffsetDateTime = now
}

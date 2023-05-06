package com.leijendary.spring.template.iam.entity

import com.leijendary.spring.template.iam.core.entity.AppEntity
import com.leijendary.spring.template.iam.core.util.RequestContext.now
import jakarta.persistence.*
import jakarta.persistence.EnumType.STRING
import org.springframework.data.annotation.CreatedDate
import java.time.OffsetDateTime

@Entity
class UserSocial : AppEntity() {
    enum class Provider(val value: String) {
        APPLE("apple"),
        FACEBOOK("facebook"),
        GOOGLE("google");
    }

    @Id
    lateinit var id: String

    @ManyToOne
    lateinit var user: User

    @Enumerated(STRING)
    lateinit var provider: Provider

    @CreatedDate
    @Column(updatable = false)
    var createdAt: OffsetDateTime = now
}

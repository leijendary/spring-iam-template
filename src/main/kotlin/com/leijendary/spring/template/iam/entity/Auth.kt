package com.leijendary.spring.template.iam.entity

import com.leijendary.spring.template.iam.core.entity.IdentityEntity
import com.leijendary.spring.template.iam.core.util.RequestContext.now
import jakarta.persistence.*
import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.EnumType.STRING
import org.springframework.data.annotation.CreatedDate
import java.time.OffsetDateTime

@Entity
class Auth : IdentityEntity() {
    @ManyToOne
    @JoinColumn
    lateinit var user: User

    lateinit var username: String
    lateinit var audience: String

    @Enumerated(STRING)
    lateinit var type: UserCredential.Type

    @OneToOne(mappedBy = "auth", cascade = [ALL], orphanRemoval = true)
    lateinit var access: AuthAccess

    @OneToOne(mappedBy = "auth", cascade = [ALL], orphanRemoval = true)
    lateinit var refresh: AuthRefresh

    @CreatedDate
    @Column(updatable = false)
    var createdAt: OffsetDateTime = now
}

package com.leijendary.spring.template.iam.entity

import com.leijendary.spring.template.iam.core.entity.IdentityEntity
import com.leijendary.spring.template.iam.core.util.RequestContext.now
import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import org.springframework.data.annotation.CreatedDate
import java.time.OffsetDateTime

@Entity
class Auth : IdentityEntity() {
    @ManyToOne
    @JoinColumn
    var user: User? = null

    var username: String = ""
    var audience: String = ""
    var type: String = ""
    var deviceId: String = ""

    @OneToOne(mappedBy = "auth", cascade = [ALL], orphanRemoval = true)
    var access: AuthAccess? = null

    @OneToOne(mappedBy = "auth", cascade = [ALL], orphanRemoval = true)
    var refresh: AuthRefresh? = null

    @CreatedDate
    var createdAt: OffsetDateTime = now
}

package com.leijendary.spring.template.iam.entity

import com.leijendary.spring.template.iam.core.entity.IdentityEntity
import com.leijendary.spring.template.iam.core.util.RequestContext.now
import com.leijendary.spring.template.iam.entity.listener.UserDeviceListener
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.ManyToOne
import org.springframework.data.annotation.CreatedDate
import java.time.OffsetDateTime

@Entity
@EntityListeners(UserDeviceListener::class)
class UserDevice : IdentityEntity() {
    @ManyToOne
    var user: User? = null

    var token: String = ""
    var platform: String = ""
    var endpoint: String? = null

    @CreatedDate
    var createdAt: OffsetDateTime = now
}

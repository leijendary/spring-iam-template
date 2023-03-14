package com.leijendary.spring.template.iam.entity

import com.leijendary.spring.template.iam.core.projection.UUIDProjection
import com.leijendary.spring.template.iam.core.util.RequestContext.now
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.OffsetDateTime
import java.util.*

@Entity
@EntityListeners(AuditingEntityListener::class)
class UserAddress : UUIDProjection {
    @Id
    @GeneratedValue
    @Column(updatable = false)
    override var id: UUID? = null

    @ManyToOne
    var user: User? = null

    var street = ""
    var city = ""
    var region = ""
    var postalCode = ""
    var country = ""
    var countryCode = ""
    var longitude = 0.0
    var latitude = 0.0

    @CreatedDate
    var createdAt: OffsetDateTime = now

    @LastModifiedDate
    var lastModifiedAt: OffsetDateTime = now
}

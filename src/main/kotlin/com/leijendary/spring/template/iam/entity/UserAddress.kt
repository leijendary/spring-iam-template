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
    lateinit var user: User

    @Column(name = "line_1")
    lateinit var line1: String

    @Column(name = "line_2")
    var line2: String? = null

    lateinit var city: String
    lateinit var region: String
    lateinit var postalCode: String
    lateinit var country: String
    lateinit var countryCode: String
    var longitude: Double = 0.0
    var latitude: Double = 0.0

    @CreatedDate
    @Column(updatable = false)
    var createdAt: OffsetDateTime = now

    @LastModifiedDate
    var lastModifiedAt: OffsetDateTime = now
}

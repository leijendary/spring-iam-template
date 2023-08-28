package com.leijendary.spring.template.iam.core.entity

import com.leijendary.spring.template.iam.core.projection.CreatedProjection
import com.leijendary.spring.template.iam.core.projection.LastModifiedProjection
import com.leijendary.spring.template.iam.core.util.RequestContext.now
import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Version
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.OffsetDateTime

@EntityListeners(AuditingEntityListener::class)
@MappedSuperclass
open class AuditingUUIDEntity : UUIDEntity(), CreatedProjection, LastModifiedProjection {
    @Version
    var version = 0

    @CreatedDate
    @Column(updatable = false)
    override var createdAt: OffsetDateTime = now

    @CreatedBy
    @Column(updatable = false)
    override var createdBy: String = ""

    @LastModifiedDate
    override var lastModifiedAt: OffsetDateTime = now

    @LastModifiedBy
    override var lastModifiedBy: String = ""
}

package com.leijendary.spring.template.iam.entity

import com.leijendary.spring.template.iam.core.entity.SoftDeleteEntity
import com.leijendary.spring.template.iam.core.entity.UUIDEntity
import com.leijendary.spring.template.iam.core.projection.CreatedProjection
import com.leijendary.spring.template.iam.core.util.RequestContext.now
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.OneToMany
import org.hibernate.annotations.Where
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.OffsetDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
@Where(clause = "deleted_at is null")
class Account : UUIDEntity(), CreatedProjection, SoftDeleteEntity {
    var type = ""
    var status = ""

    @OneToMany(mappedBy = "account")
    val users: Set<User> = HashSet()

    @CreatedDate
    override var createdAt: OffsetDateTime = now

    @CreatedBy
    override var createdBy: String = ""

    override var deletedAt: OffsetDateTime? = null
    override var deletedBy: String? = null

    enum class Type(val value: String) {
        CUSTOMER("customer"),
        PARTNER("partner");

        override fun toString() = value
    }
}

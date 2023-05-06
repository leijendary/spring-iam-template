package com.leijendary.spring.template.iam.entity

import com.leijendary.spring.template.iam.core.entity.SoftDeleteEntity
import com.leijendary.spring.template.iam.core.entity.UUIDEntity
import com.leijendary.spring.template.iam.core.projection.CreatedProjection
import com.leijendary.spring.template.iam.core.util.RequestContext.now
import com.leijendary.spring.template.iam.model.Status
import jakarta.persistence.*
import jakarta.persistence.EnumType.STRING
import org.hibernate.annotations.Where
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.OffsetDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
@Where(clause = "deleted_at is null")
class Account : UUIDEntity(), CreatedProjection, SoftDeleteEntity {
    enum class Type(val value: String) {
        CUSTOMER("customer");
    }

    @Enumerated(STRING)
    lateinit var type: Type

    @Enumerated(STRING)
    lateinit var status: Status

    @OneToMany(mappedBy = "account")
    val users: Set<User> = HashSet()

    @CreatedDate
    @Column(updatable = false)
    override var createdAt: OffsetDateTime = now

    @CreatedBy
    @Column(updatable = false)
    override var createdBy: String = ""

    override var deletedAt: OffsetDateTime? = null
    override var deletedBy: String? = null
}

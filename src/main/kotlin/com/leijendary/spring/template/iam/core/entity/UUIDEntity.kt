package com.leijendary.spring.template.iam.core.entity

import com.leijendary.spring.template.iam.core.projection.UUIDProjection
import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import java.util.*

@MappedSuperclass
open class UUIDEntity : AppEntity(), UUIDProjection {
    @Id
    @GeneratedValue
    @Column(updatable = false)
    override var id: UUID? = null

    @Column(insertable = false, updatable = false)
    var rowId: Long = 0
}

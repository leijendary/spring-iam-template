package com.leijendary.spring.template.iam.entity

import com.leijendary.spring.template.iam.core.entity.IdentityEntity
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne

@Entity
class RolePermission : IdentityEntity() {
    @OneToOne
    @JoinColumn
    var role: Role? = null

    @OneToOne
    @JoinColumn
    var permission: Permission? = null
}

package com.leijendary.spring.template.iam.entity

import com.leijendary.spring.template.iam.core.entity.IdentityEntity
import jakarta.persistence.Entity
import jakarta.persistence.OneToOne

@Entity
class RolePermission : IdentityEntity() {
    @OneToOne
    lateinit var role: Role

    @OneToOne
    lateinit var permission: Permission
}

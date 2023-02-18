package com.leijendary.spring.template.iam.entity

import com.leijendary.spring.template.iam.core.entity.IdentityEntity
import jakarta.persistence.Entity

@Entity
class Permission : IdentityEntity() {
    var permission = ""
}

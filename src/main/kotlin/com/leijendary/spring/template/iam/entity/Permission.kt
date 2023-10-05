package com.leijendary.spring.template.iam.entity

import com.leijendary.spring.template.iam.core.entity.IdentityEntity
import jakarta.persistence.Entity

@Entity
class Permission : IdentityEntity() {
    companion object {
        @JvmStatic
        val serialVersionUID = -1L
    }

    lateinit var value: String
}

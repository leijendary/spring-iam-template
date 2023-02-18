package com.leijendary.spring.template.iam.entity

import com.leijendary.spring.template.iam.core.entity.AuditingUUIDEntity
import jakarta.persistence.Entity
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany

@Entity
class Role : AuditingUUIDEntity() {
    var name = ""
    var description: String? = null

    @ManyToMany
    @JoinTable(name = "role_permission")
    val permissions: MutableSet<Permission> = HashSet()

    enum class Defaults(val value: String) {
        CUSTOMER("Customer"),
        ADMIN("Admin"),
        PARTNER("Partner");

        override fun toString() = value
    }
}

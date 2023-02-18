package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.entity.Role
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface RoleRepository : JpaRepository<Role, UUID> {
    fun findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        name: String,
        description: String,
        pageable: Pageable
    ): Page<Role>

    fun findFirstByName(name: String): Role?
}

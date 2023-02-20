package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.entity.Permission
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface PermissionRepository : JpaRepository<Permission, Long> {
    fun findAllByValueContainingIgnoreCase(value: String, pageable: Pageable): Page<Permission>
}

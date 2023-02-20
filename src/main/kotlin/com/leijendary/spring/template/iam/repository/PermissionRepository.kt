package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.core.exception.ResourceNotFoundException
import com.leijendary.spring.template.iam.entity.Permission
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull

private val source = listOf("data", "Permission", "id")

interface PermissionRepository : JpaRepository<Permission, Long> {
    fun findAllByValueContainingIgnoreCase(value: String, pageable: Pageable): Page<Permission>

    fun findByIdOrThrow(id: Long) = findByIdOrNull(id) ?: throw ResourceNotFoundException(source, id)
}

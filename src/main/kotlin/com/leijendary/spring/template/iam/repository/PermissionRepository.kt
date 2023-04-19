package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.core.exception.ResourceNotFoundException
import com.leijendary.spring.template.iam.entity.Permission
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional

private val source = listOf("data", "Permission", "id")

interface PermissionRepository : JpaRepository<Permission, Long> {
    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<Permission>

    @Transactional(readOnly = true)
    fun findAllByValueContainingIgnoreCase(value: String, pageable: Pageable): Page<Permission>

    @Transactional(readOnly = true)
    fun findByIdOrThrow(id: Long) = findByIdOrNull(id) ?: throw ResourceNotFoundException(source, id)
}

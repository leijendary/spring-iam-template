package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.core.exception.ResourceNotFoundException
import com.leijendary.spring.template.iam.entity.Role
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import java.util.*

private val sourceId = listOf("data", "Role", "id")
private val sourceName = listOf("data", "Role", "name")

interface RoleRepository : JpaRepository<Role, UUID> {
    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<Role>

    @Transactional(readOnly = true)
    fun findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        name: String,
        description: String,
        pageable: Pageable
    ): Page<Role>

    @Transactional(readOnly = true)
    fun findFirstByName(name: String): Role?

    @Transactional(readOnly = true)
    fun findFirstByNameOrThrow(name: String): Role {
        return findFirstByName(name) ?: throw ResourceNotFoundException(sourceName, name)
    }

    @Transactional(readOnly = true)
    fun findByIdOrThrow(id: UUID) = findByIdOrNull(id) ?: throw ResourceNotFoundException(sourceId, id)
}

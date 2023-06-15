package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.core.exception.ResourceNotFoundException
import com.leijendary.spring.template.iam.core.repository.SoftDeleteRepository
import com.leijendary.spring.template.iam.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import java.util.*

private val source = listOf("data", "User", "id")

interface UserRepository : JpaRepository<User, UUID>, SoftDeleteRepository<User>, JpaSpecificationExecutor<User> {
    @Transactional(readOnly = true)
    override fun findAll(specification: Specification<User>, pageable: Pageable): Page<User>

    @Transactional(readOnly = true)
    fun findByIdOrThrow(id: UUID) = findByIdOrNull(id) ?: throw ResourceNotFoundException(source, id)
}

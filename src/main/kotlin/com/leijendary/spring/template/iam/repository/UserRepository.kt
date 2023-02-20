package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.core.exception.ResourceNotFoundException
import com.leijendary.spring.template.iam.core.repository.SoftDeleteRepository
import com.leijendary.spring.template.iam.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.findByIdOrNull
import java.util.*

private val source = listOf("data", "User", "id")

interface UserRepository : JpaRepository<User, UUID>, SoftDeleteRepository<User>, JpaSpecificationExecutor<User> {
    fun findByIdOrThrow(id: UUID) = findByIdOrNull(id) ?: throw ResourceNotFoundException(source, id)
}

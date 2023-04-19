package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.core.exception.ResourceNotFoundException
import com.leijendary.spring.template.iam.entity.UserAddress
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import java.util.*

private val source = listOf("data", "UserAddress", "id")

interface UserAddressRepository : JpaRepository<UserAddress, UUID> {
    @Transactional(readOnly = true)
    fun findByUserId(userId: UUID, pageable: Pageable): Page<UserAddress>

    @Transactional(readOnly = true)
    fun findFirstByIdAndUserId(id: UUID, userId: UUID): UserAddress?

    @Transactional(readOnly = true)
    fun findFirstByIdAndUserIdOrThrow(id: UUID, userId: UUID): UserAddress {
        return findFirstByIdAndUserId(id, userId) ?: throw ResourceNotFoundException(source, id)
    }
}
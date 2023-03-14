package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.core.exception.ResourceNotFoundException
import com.leijendary.spring.template.iam.entity.UserAddress
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

private val source = listOf("data", "UserAddress", "id")

interface UserAddressRepository : JpaRepository<UserAddress, UUID> {
    fun findByUserId(userId: UUID, pageable: Pageable): Page<UserAddress>

    fun findFirstByIdAndUserId(id: UUID, userId: UUID): UserAddress?

    fun findFirstByIdAndUserIdOrThrow(id: UUID, userId: UUID): UserAddress {
        return findFirstByIdAndUserId(id, userId) ?: throw ResourceNotFoundException(source, id)
    }
}
package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.core.exception.ResourceNotFoundException
import com.leijendary.spring.template.iam.core.repository.SoftDeleteRepository
import com.leijendary.spring.template.iam.entity.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import java.util.*

private val sourceUsersId = listOf("data", "Account", "users", "id")

interface AccountRepository : JpaRepository<Account, UUID>, SoftDeleteRepository<Account> {
    @Transactional(readOnly = true)
    fun findFirstByUsersId(userId: UUID): Account?

    @Transactional(readOnly = true)
    fun findFirstByUsersIdOrThrow(userId: UUID): Account {
        return findFirstByUsersId(userId) ?: throw ResourceNotFoundException(sourceUsersId, userId.toString())
    }
}

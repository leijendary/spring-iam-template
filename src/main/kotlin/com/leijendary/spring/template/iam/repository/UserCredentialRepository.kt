package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.core.exception.InvalidCredentialException
import com.leijendary.spring.template.iam.entity.UserCredential
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional

interface UserCredentialRepository : JpaRepository<UserCredential, Long> {
    @Transactional(readOnly = true)
    fun findFirstByUsernameAndUserDeletedAtIsNull(username: String): UserCredential?

    @Transactional(readOnly = true)
    fun findFirstByUsernameAndTypeAndUserDeletedAtIsNull(username: String, type: UserCredential.Type): UserCredential?

    @Transactional(readOnly = true)
    fun findFirstByUsernameAndUserDeletedAtIsNullOrThrow(username: String): UserCredential {
        return findFirstByUsernameAndUserDeletedAtIsNull(username) ?: throw InvalidCredentialException()
    }

    @Transactional(readOnly = true)
    fun findFirstByUsernameAndTypeAndUserDeletedAtIsNullOrThrow(
        username: String,
        type: UserCredential.Type
    ): UserCredential {
        return findFirstByUsernameAndTypeAndUserDeletedAtIsNull(username, type) ?: throw InvalidCredentialException()
    }
}

package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.core.exception.InvalidCredentialException
import com.leijendary.spring.template.iam.entity.UserCredential
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional

interface UserCredentialRepository : JpaRepository<UserCredential, Long> {
    @Transactional(readOnly = true)
    fun findFirstByUsernameIgnoreCaseAndUserDeletedAtIsNull(username: String): UserCredential?

    @Transactional(readOnly = true)
    fun findFirstByUsernameIgnoreCaseAndTypeAndUserDeletedAtIsNull(
        username: String,
        type: UserCredential.Type
    ): UserCredential?

    @Transactional(readOnly = true)
    fun findFirstByUsernameIgnoreCaseAndUserDeletedAtIsNullOrThrow(username: String): UserCredential {
        return findFirstByUsernameIgnoreCaseAndUserDeletedAtIsNull(username) ?: throw InvalidCredentialException()
    }

    @Transactional(readOnly = true)
    fun findFirstByUsernameIgnoreCaseAndTypeAndUserDeletedAtIsNullOrThrow(
        username: String,
        type: UserCredential.Type
    ): UserCredential {
        return findFirstByUsernameIgnoreCaseAndTypeAndUserDeletedAtIsNull(username, type)
            ?: throw InvalidCredentialException()
    }
}

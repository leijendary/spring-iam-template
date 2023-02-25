package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.core.exception.InvalidCredentialException
import com.leijendary.spring.template.iam.entity.UserCredential
import org.springframework.data.jpa.repository.JpaRepository

interface UserCredentialRepository : JpaRepository<UserCredential, Long> {
    fun findFirstByUsernameAndUserDeletedAtIsNull(username: String): UserCredential?

    fun findFirstByUsernameAndUserDeletedAtIsNullOrThrow(username: String): UserCredential {
        return findFirstByUsernameAndUserDeletedAtIsNull(username) ?: throw InvalidCredentialException()
    }
}

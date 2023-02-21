package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.core.exception.ResourceNotFoundException
import com.leijendary.spring.template.iam.entity.Verification
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

private val sourceCode = listOf("data", "Verification", "code")

interface VerificationRepository : JpaRepository<Verification, Long> {
    fun deleteAllByUserIdAndType(userId: UUID, type: String)

    fun findFirstByCodeAndType(code: String, type: String): Verification?

    fun findFirstByCodeAndTypeAndUserId(code: String, type: String, userId: UUID): Verification?

    fun findFirstByCodeAndTypeOrThrow(code: String, type: String): Verification {
        return findFirstByCodeAndType(code, type)
            ?: throw ResourceNotFoundException(sourceCode, code)
    }

    fun findFirstByCodeAndTypeAndUserIdOrThrow(code: String, type: String, userId: UUID): Verification {
        return findFirstByCodeAndTypeAndUserId(code, type, userId)
            ?: throw ResourceNotFoundException(sourceCode, code)
    }
}

package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.core.exception.ResourceNotFoundException
import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.entity.Verification
import org.springframework.data.jpa.repository.JpaRepository

private val sourceCode = listOf("data", "Verification", "code")

interface VerificationRepository : JpaRepository<Verification, Long> {
    fun findFirstByCodeAndType(code: String, type: Verification.Type): Verification?

    fun findFirstByCodeAndTypeOrThrow(code: String, type: Verification.Type): Verification {
        return findFirstByCodeAndType(code, type) ?: throw ResourceNotFoundException(sourceCode, code)
    }

    fun findFirstByFieldAndValueAndCodeAndType(
        field: UserCredential.Type,
        value: String,
        code: String,
        type: Verification.Type
    ): Verification?

    fun findFirstByFieldAndValueAndCodeAndAndTypeOrThrow(
        field: UserCredential.Type,
        value: String,
        code: String,
        type: Verification.Type
    ): Verification {
        return findFirstByFieldAndValueAndCodeAndType(field, value, code, type)
            ?: throw ResourceNotFoundException(sourceCode, code)
    }

    fun deleteAllByFieldAndValueAndType(field: UserCredential.Type, value: String, type: Verification.Type)
}

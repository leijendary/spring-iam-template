package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.core.exception.ResourceNotFoundException
import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.entity.Verification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional

private val sourceCode = listOf("data", "Verification", "code")

interface VerificationRepository : JpaRepository<Verification, Long> {
    @Transactional(readOnly = true)
    fun findFirstByCodeAndType(code: String, type: Verification.Type): Verification?

    @Transactional(readOnly = true)
    fun findFirstByCodeAndTypeOrThrow(code: String, type: Verification.Type): Verification {
        return findFirstByCodeAndType(code, type) ?: throw ResourceNotFoundException(sourceCode, code)
    }

    @Transactional(readOnly = true)
    fun findFirstByFieldAndValueAndCodeAndType(
        field: UserCredential.Type,
        value: String,
        code: String,
        type: Verification.Type
    ): Verification?

    @Transactional(readOnly = true)
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

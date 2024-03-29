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
    fun findFirstByFieldAndValueIgnoreCaseAndCodeAndType(
        field: UserCredential.Type,
        value: String,
        code: String,
        type: Verification.Type
    ): Verification?

    @Transactional(readOnly = true)
    fun findFirstByFieldAndValueIgnoreCaseAndCodeAndTypeOrThrow(
        field: UserCredential.Type,
        value: String,
        code: String,
        type: Verification.Type
    ): Verification {
        return findFirstByFieldAndValueIgnoreCaseAndCodeAndType(field, value, code, type)
            ?: throw ResourceNotFoundException(sourceCode, code)
    }

    @Transactional(readOnly = true)
    fun findFirstByFieldAndValueIgnoreCaseAndType(
        field: UserCredential.Type,
        value: String,
        type: Verification.Type
    ): Verification?

    fun deleteAllByFieldAndValueIgnoreCaseAndType(field: UserCredential.Type, value: String, type: Verification.Type)
}

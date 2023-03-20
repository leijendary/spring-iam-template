package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.core.exception.ResourceNotFoundException
import com.leijendary.spring.template.iam.entity.Verification
import org.springframework.data.jpa.repository.JpaRepository

private val sourceCode = listOf("data", "Verification", "code")

interface VerificationRepository : JpaRepository<Verification, Long> {
    fun findFirstByCodeAndType(code: String, type: String): Verification?

    fun findFirstByCodeAndTypeOrThrow(code: String, type: String): Verification {
        return findFirstByCodeAndType(code, type) ?: throw ResourceNotFoundException(sourceCode, code)
    }

    fun findFirstByFieldAndValueAndCodeAndType(field: String, value: String, code: String, type: String): Verification?

    fun findFirstByFieldAndValueAndCodeAndAndTypeOrThrow(
        field: String,
        value: String,
        code: String,
        type: String
    ): Verification {
        return findFirstByFieldAndValueAndCodeAndType(field, value, code, type)
            ?: throw ResourceNotFoundException(sourceCode, code)
    }
    
    fun deleteAllByFieldAndValueAndType(field: String, value: String, type: String)
}

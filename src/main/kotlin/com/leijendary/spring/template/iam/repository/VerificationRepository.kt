package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.core.exception.ResourceNotFoundException
import com.leijendary.spring.template.iam.entity.Verification
import org.springframework.data.jpa.repository.JpaRepository

private val sourceCode = listOf("data", "Verification", "code")

interface VerificationRepository : JpaRepository<Verification, Long> {
    fun deleteAllByFieldAndValueAndType(field: String, value: String, type: String)

    fun findFirstByCodeAndType(code: String, type: String): Verification?

    fun findFirstByCodeAndTypeOrThrow(code: String, type: String): Verification {
        return findFirstByCodeAndType(code, type) ?: throw ResourceNotFoundException(sourceCode, code)
    }

    fun findFirstByFieldAndValueAndCodeAndDeviceIdAndType(
        field: String,
        value: String,
        code: String,
        deviceId: String,
        type: String
    ): Verification?

    fun findFirstByFieldAndValueAndCodeAndDeviceIdAndTypeOrThrow(
        field: String,
        value: String,
        code: String,
        deviceId: String,
        type: String
    ): Verification {
        return findFirstByFieldAndValueAndCodeAndDeviceIdAndType(field, value, code, deviceId, type)
            ?: throw ResourceNotFoundException(sourceCode, code)
    }
}

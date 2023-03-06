package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.core.exception.ResourceNotFoundException
import com.leijendary.spring.template.iam.entity.Verification
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

private val sourceCode = listOf("data", "Verification", "code")

interface VerificationRepository : JpaRepository<Verification, Long> {
    fun deleteAllByFieldAndValueAndType(field: String, value: String, type: String)

    fun findFirstByCodeAndType(code: String, type: String): Verification?

    fun deleteAllByUserIdAndType(userId: UUID, type: String)

    fun findFirstByCodeAndTypeAndDeviceId(code: String, type: String, deviceId: String): Verification?

    fun findFirstByCodeAndTypeOrThrow(code: String, type: String): Verification {
        return findFirstByCodeAndType(code, type) ?: throw ResourceNotFoundException(sourceCode, code)
    }

    fun findFirstByCodeAndTypeAndDeviceIdAndUserId(
        code: String,
        type: String,
        deviceId: String,
        userId: UUID
    ): Verification?

    fun findFirstByCodeAndTypeAndDeviceIdOrThrow(code: String, type: String, deviceId: String): Verification {
        return findFirstByCodeAndTypeAndDeviceId(code, type, deviceId)
            ?: throw ResourceNotFoundException(sourceCode, code)
    }

    fun findFirstByCodeAndTypeAndDeviceIdAndUserIdOrThrow(
        code: String,
        type: String,
        deviceId: String,
        userId: UUID
    ): Verification {
        return findFirstByCodeAndTypeAndDeviceIdAndUserId(code, type, deviceId, userId)
            ?: throw ResourceNotFoundException(sourceCode, code)
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

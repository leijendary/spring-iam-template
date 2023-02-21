package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.core.exception.ResourceNotFoundException
import com.leijendary.spring.template.iam.entity.Verification
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

private val sourceCode = listOf("data", "Verification", "code")

interface VerificationRepository : JpaRepository<Verification, Long> {
    fun deleteAllByUserIdAndType(userId: UUID, type: String)

    fun findFirstByCodeAndTypeAndDeviceId(code: String, type: String, deviceId: String): Verification?

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
}

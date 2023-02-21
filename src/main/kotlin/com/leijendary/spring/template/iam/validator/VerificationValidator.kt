package com.leijendary.spring.template.iam.validator

import com.leijendary.spring.template.iam.core.exception.StatusException
import com.leijendary.spring.template.iam.core.extension.transactional
import com.leijendary.spring.template.iam.core.util.RequestContext.now
import com.leijendary.spring.template.iam.core.util.RequestContext.userIdOrNull
import com.leijendary.spring.template.iam.entity.Verification
import com.leijendary.spring.template.iam.repository.VerificationRepository
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.GONE
import org.springframework.stereotype.Component
import java.util.*

@Component
class VerificationValidator(private val verificationRepository: VerificationRepository) {
    fun validate(code: String, type: String, deviceId: String): Verification {
        val userId = userIdOrNull?.let { UUID.fromString(it) }
        val verification = transactional(readOnly = true) {
            if (userId != null) {
                verificationRepository
                    .findFirstByCodeAndTypeAndUserIdOrThrow(code, type, userId)
            } else {
                verificationRepository
                    .findFirstByCodeAndTypeOrThrow(code, type)
            }
        }!!
        val expiresAt = verification.expiresAt!!

        // Verification is already expired
        if (expiresAt.isBefore(now)) {
            // Remove the verification since it is no longer valid
            verificationRepository.delete(verification)

            val source = listOf("data", "Verification", "expiry")

            throw StatusException(source, "validation.verification.expired", GONE)
        }

        // Device ID should match
        if (deviceId != verification.deviceId) {
            val source = listOf("data", "Verification", "deviceId")

            throw StatusException(source, "validation.deviceId.notMatch", BAD_REQUEST)
        }

        // Record is already verified, and we don't need this anymore.
        verificationRepository.delete(verification)

        return verification
    }
}

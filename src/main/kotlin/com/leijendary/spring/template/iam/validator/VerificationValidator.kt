package com.leijendary.spring.template.iam.validator

import com.leijendary.spring.template.iam.core.exception.ResourceNotFoundException
import com.leijendary.spring.template.iam.core.exception.StatusException
import com.leijendary.spring.template.iam.core.extension.transactional
import com.leijendary.spring.template.iam.core.util.RequestContext.now
import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.entity.Verification
import com.leijendary.spring.template.iam.repository.VerificationRepository
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.GONE
import org.springframework.stereotype.Component

@Component
class VerificationValidator(private val verificationRepository: VerificationRepository) {
    companion object {
        private val SOURCE = listOf("data", "Verification", "code")
    }

    fun validate(code: String, type: String, deviceId: String): Verification {
        val verification = transactional(readOnly = true) {
            verificationRepository
                .findFirstByCodeAndType(code, type)
                ?: throw ResourceNotFoundException(SOURCE, code)
        }!!
        val expiresAt = verification.expiresAt!!

        // Verification is already expired
        if (expiresAt.isBefore(now)) {
            // Remove the verification since it is no longer valid
            verificationRepository.delete(verification)

            val source = listOf("data", "Verification", "expiry")

            throw StatusException(source, "validation.verification.expired", GONE)
        }

        val field = verification.field

        // If the validation is for a phone, the deviceId should match.
        if (field == UserCredential.Type.PHONE.value && deviceId != verification.deviceId) {
            val source = listOf("data", "Verification", "deviceId")

            throw StatusException(source, "validation.deviceId.notMatch", BAD_REQUEST)
        }

        // Record is already verified, and we don't need this anymore.
        verificationRepository.delete(verification)

        return verification
    }
}

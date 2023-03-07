package com.leijendary.spring.template.iam.validator

import com.leijendary.spring.template.iam.core.exception.StatusException
import com.leijendary.spring.template.iam.core.extension.transactional
import com.leijendary.spring.template.iam.core.util.RequestContext.now
import com.leijendary.spring.template.iam.entity.Verification
import com.leijendary.spring.template.iam.repository.VerificationRepository
import org.springframework.http.HttpStatus.GONE
import org.springframework.stereotype.Component

@Component
class VerificationValidator(private val verificationRepository: VerificationRepository) {
    /**
     * Validate that the [field]-[value]-[code]-[deviceId]-[type] combination exists and
     * is not expired. This method will throw an error if the verification is not found.
     */
    fun validateByField(
        field: String,
        value: String,
        code: String,
        deviceId: String,
        type: Verification.Type
    ): Verification {
        val verification = transactional(readOnly = true) {
            verificationRepository
                .findFirstByFieldAndValueAndCodeAndDeviceIdAndTypeOrThrow(field, value, code, deviceId, type.value)
        }!!

        validateExpiration(verification)

        return verification
    }

    /**
     * Validate that the [code]-[type] combination exists and is not expired.
     * This method will throw an error if the verification is not found.
     *
     * This is recommended for verification UUID codes.
     */
    fun validateByCode(code: String, type: Verification.Type): Verification {
        val verification = transactional(readOnly = true) {
            verificationRepository.findFirstByCodeAndTypeOrThrow(code, type.value)
        }!!

        validateExpiration(verification)

        return verification
    }

    private fun validateExpiration(verification: Verification) {
        val expiresAt = verification.expiresAt!!

        // Verification is not yet expired
        if (expiresAt.isAfter(now)) {
            return
        }

        val source = listOf("data", "Verification", "expiry")

        throw StatusException(source, "validation.verification.expired", GONE)
    }
}

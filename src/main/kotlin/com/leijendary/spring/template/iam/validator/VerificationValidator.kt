package com.leijendary.spring.template.iam.validator

import com.leijendary.spring.template.iam.core.exception.StatusException
import com.leijendary.spring.template.iam.core.util.RequestContext.now
import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.entity.Verification
import com.leijendary.spring.template.iam.entity.Verification.Type
import com.leijendary.spring.template.iam.repository.VerificationRepository
import org.springframework.http.HttpStatus.GONE
import org.springframework.stereotype.Component

private val expirySource = listOf("data", "Verification", "expiry")

@Component
class VerificationValidator(private val verificationRepository: VerificationRepository) {
    /**
     * Validate that the [field]-[value]-[code]-[type] combination exists and
     * is not expired. This method will throw an error if the verification is not found.
     */
    fun validateByField(field: UserCredential.Type, value: String, code: String, type: Type): Verification {
        val verification = verificationRepository
            .findFirstByFieldAndValueIgnoreCaseAndCodeAndTypeOrThrow(field, value, code, type)

        validateExpiration(verification)

        return verification
    }

    /**
     * Validate that the [code]-[type] combination exists and is not expired.
     * This method will throw an error if the verification is not found.
     *
     * This is recommended for verification UUID codes.
     */
    fun validateByCode(code: String, type: Type): Verification {
        val verification = verificationRepository.findFirstByCodeAndTypeOrThrow(code, type)

        validateExpiration(verification)

        return verification
    }

    private fun validateExpiration(verification: Verification) {
        val expiresAt = verification.expiresAt

        // Verification is not yet expired
        if (expiresAt.isAfter(now)) {
            return
        }

        throw StatusException(expirySource, "validation.verification.expired", GONE)
    }
}

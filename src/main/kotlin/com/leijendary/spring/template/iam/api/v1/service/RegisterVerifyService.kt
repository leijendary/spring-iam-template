package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.model.NextCode
import com.leijendary.spring.template.iam.api.v1.model.VerifyRequest
import com.leijendary.spring.template.iam.generator.CodeGenerationStrategy
import com.leijendary.spring.template.iam.repository.VerificationRepository
import com.leijendary.spring.template.iam.util.VerificationType
import com.leijendary.spring.template.iam.validator.VerificationValidator
import org.springframework.stereotype.Service

@Service
class RegisterVerifyService(
    private val verificationRepository: VerificationRepository,
    private val verificationValidator: VerificationValidator
) {
    fun verify(request: VerifyRequest): NextCode {
        val verification = verificationValidator.validate(
            request.code!!,
            VerificationType.REGISTRATION,
            request.deviceId!!
        )
        val hasPassword = verification
            .user!!
            .credentials
            .any {
                it.type == verification.field
            }

        if (hasPassword) {
            return NextCode(VerificationType.AUTHENTICATE, null)
        }

        val code = CodeGenerationStrategy.UUID_STRATEGY.generate()

        // Reuse object but change other values and reset ID.
        verification.apply {
            id = 0
            type = VerificationType.NOMINATE_PASSWORD
            this.code = code
        }

        verificationRepository.save(verification)

        return NextCode(VerificationType.NOMINATE_PASSWORD, code)
    }
}

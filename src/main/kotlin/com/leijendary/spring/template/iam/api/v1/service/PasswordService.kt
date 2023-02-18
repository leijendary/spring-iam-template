package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.model.NextCode
import com.leijendary.spring.template.iam.api.v1.model.PasswordNominateRequest
import com.leijendary.spring.template.iam.api.v1.model.PasswordResetRequest
import com.leijendary.spring.template.iam.api.v1.model.PasswordResetVerifyRequest
import com.leijendary.spring.template.iam.core.config.properties.VerificationProperties
import com.leijendary.spring.template.iam.core.extension.transactional
import com.leijendary.spring.template.iam.core.util.RequestContext.now
import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.entity.Verification
import com.leijendary.spring.template.iam.event.CredentialEvent
import com.leijendary.spring.template.iam.generator.CodeGenerationStrategy
import com.leijendary.spring.template.iam.repository.UserCredentialRepository
import com.leijendary.spring.template.iam.repository.UserRepository
import com.leijendary.spring.template.iam.repository.VerificationRepository
import com.leijendary.spring.template.iam.util.Status
import com.leijendary.spring.template.iam.util.VerificationType
import com.leijendary.spring.template.iam.validator.VerificationValidator
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class PasswordService(
    private val credentialEvent: CredentialEvent,
    private val passwordEncoder: PasswordEncoder,
    private val userCredentialRepository: UserCredentialRepository,
    private val userRepository: UserRepository,
    private val verificationProperties: VerificationProperties,
    private val verificationRepository: VerificationRepository,
    private val verificationValidator: VerificationValidator
) {
    fun reset(request: PasswordResetRequest): NextCode {
        val credential = userCredentialRepository.findFirstByUsername(request.username!!)
        val field = credential.type
        val user = credential.user!!
        val generator = CodeGenerationStrategy.fromField(credential.type)
        val code = generator.generate()
        val verification = Verification()
            .apply {
                this.user = user
                this.code = code
                this.field = field
                deviceId = request.deviceId!!
                type = VerificationType.RESET_PASSWORD
                expiry = now.plus(verificationProperties.expiry)
            }
            .let {
                transactional {
                    // Remove old verifications
                    verificationRepository.deleteAllByUserIdAndType(it.user!!.id!!, it.type)
                    verificationRepository.save(it)
                }
            }

        credentialEvent.verify(verification)

        return NextCode(VerificationType.VERIFICATION, null)
    }

    fun resetVerify(request: PasswordResetVerifyRequest): NextCode {
        val verification = verificationValidator.validate(
            request.code!!,
            VerificationType.RESET_PASSWORD,
            request.deviceId!!
        )

        // Reuse object but change other values and reset ID.
        verification.apply {
            id = 0
            type = VerificationType.NOMINATE_PASSWORD
            code = CodeGenerationStrategy.UUID_STRATEGY.generate()
        }

        verificationRepository.save(verification)

        return NextCode(VerificationType.NOMINATE_PASSWORD, verification.code)
    }

    fun nominate(request: PasswordNominateRequest): NextCode {
        val verification = verificationValidator.validate(
            request.code!!,
            VerificationType.NOMINATE_PASSWORD,
            request.deviceId!!
        )
        val user = verification.user!!
        val field = verification.field
        val password = passwordEncoder.encode(request.password!!)
        val credentials = user
            .credentials
            .filter {
                it.type == field
            }
            .map {
                it.password = password

                it
            }
            .toMutableList()

        if (credentials.isEmpty()) {
            val username = user.getUsername(field)
            val credential = UserCredential().apply {
                this.user = user
                this.username = username
                this.type = field
                this.password = password
            }

            credentials.add(credential)
        }

        user.status = if (user.isIncomplete) Status.INCOMPLETE else Status.ACTIVE

        transactional {
            userCredentialRepository.saveAll(credentials)
            userRepository.save(user)
        }

        return NextCode(VerificationType.AUTHENTICATE, null)
    }
}

package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.model.*
import com.leijendary.spring.template.iam.core.config.properties.VerificationProperties
import com.leijendary.spring.template.iam.core.exception.InvalidCredentialException
import com.leijendary.spring.template.iam.core.extension.transactional
import com.leijendary.spring.template.iam.core.util.RequestContext.userId
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
import org.springframework.transaction.annotation.Transactional
import java.util.*

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
        val credential = transactional(readOnly = true) {
            userCredentialRepository
                .findFirstByUsernameAndUserDeletedAtIsNull(request.username!!)
                ?: throw InvalidCredentialException()
        }!!
        val field = credential.type
        val user = credential.user!!
        val generator = CodeGenerationStrategy.fromField(credential.type)
        val code = generator.generate()
        val deviceId = request.deviceId!!
        val verification = Verification()
            .apply {
                this.user = user
                this.code = code
                this.field = field
                this.deviceId = deviceId
                type = VerificationType.PASSWORD_RESET
                expiresAt = verificationProperties.computeExpiration()
            }
            .let {
                transactional {
                    // Remove old verifications
                    verificationRepository.deleteAllByUserIdAndType(it.user!!.id!!, it.type)
                    verificationRepository.save(it)
                }!!
            }

        credentialEvent.verify(verification)

        return NextCode(VerificationType.VERIFICATION)
    }

    fun resetVerify(request: PasswordVerifyRequest): NextCode {
        val verification = verificationValidator.validate(
            request.code!!,
            VerificationType.PASSWORD_RESET,
            request.deviceId!!
        )

        // Reuse object but change other values and reset ID.
        verification.apply {
            id = 0
            type = VerificationType.PASSWORD_NOMINATE
            code = CodeGenerationStrategy.UUID_STRATEGY.generate()
        }

        verificationRepository.save(verification)

        return NextCode(VerificationType.PASSWORD_NOMINATE, verification.code)
    }

    fun nominate(request: PasswordNominateRequest): NextCode {
        val verification = verificationValidator.validate(
            request.code!!,
            VerificationType.PASSWORD_NOMINATE,
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

        user.apply {
            status = if (user.isIncomplete) Status.INCOMPLETE else Status.ACTIVE
            setVerified(field)
        }

        transactional {
            userCredentialRepository.saveAll(credentials)
            userRepository.save(user)
        }

        return NextCode(VerificationType.AUTHENTICATE)
    }

    fun changeInitiate(request: PasswordChangeInitiateRequest): NextCode {
        val id = UUID.fromString(userId)
        val user = transactional(readOnly = true) {
            userRepository.findByIdOrThrow(id)
        }!!
        val field = request.preferredUsername!!
        val generator = CodeGenerationStrategy.fromField(field)
        val code = generator.generate()
        val deviceId = request.deviceId!!
        val verification = Verification()
            .apply {
                this.user = user
                this.code = code
                this.field = field
                this.deviceId = deviceId
                type = VerificationType.PASSWORD_CHANGE_VERIFY
                expiresAt = verificationProperties.computeExpiration()
            }
            .let {
                transactional {
                    // Remove old verifications
                    verificationRepository.deleteAllByUserIdAndType(user.id!!, it.type)
                    verificationRepository.save(it)
                }!!
            }

        credentialEvent.verify(verification)

        return NextCode(VerificationType.PASSWORD_CHANGE_VERIFY)
    }

    fun changeVerify(request: PasswordVerifyRequest): NextCode {
        val verification = verificationValidator.validate(
            request.code!!,
            VerificationType.PASSWORD_CHANGE_VERIFY,
            request.deviceId!!
        )

        // Reuse object but change other values and reset ID.
        verification.apply {
            id = 0
            type = VerificationType.PASSWORD_CHANGE
            code = CodeGenerationStrategy.UUID_STRATEGY.generate()
        }

        verificationRepository.save(verification)

        return NextCode(VerificationType.PASSWORD_CHANGE, verification.code)
    }

    @Transactional
    fun change(request: PasswordChangeRequest): NextCode {
        val verification = verificationValidator.validate(
            request.code!!,
            VerificationType.PASSWORD_CHANGE,
            request.deviceId!!
        )
        val field = verification.field
        val currentPassword = request.currentPassword!!
        val password = request.password!!
        val user = verification.user!!
        val username = user.getUsername(field)
        val credential = user
            .credentials
            .firstOrNull { it.username == username }
            ?: throw InvalidCredentialException()

        // Validate if the current password matches.
        if (!passwordEncoder.matches(currentPassword, credential.password)) {
            throw InvalidCredentialException()
        }

        credential.password = passwordEncoder.encode(password)

        userCredentialRepository.save(credential)

        return NextCode(VerificationType.AUTHENTICATE)
    }
}

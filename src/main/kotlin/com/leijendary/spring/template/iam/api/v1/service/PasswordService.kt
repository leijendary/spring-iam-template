package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.model.Next
import com.leijendary.spring.template.iam.api.v1.model.Next.Type.AUTHENTICATE
import com.leijendary.spring.template.iam.api.v1.model.PasswordChangeRequest
import com.leijendary.spring.template.iam.api.v1.model.PasswordNominateRequest
import com.leijendary.spring.template.iam.api.v1.model.PasswordResetRequest
import com.leijendary.spring.template.iam.core.datasource.transactional
import com.leijendary.spring.template.iam.core.exception.InvalidCredentialException
import com.leijendary.spring.template.iam.core.util.RequestContext.userIdOrThrow
import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.entity.Verification.Type.PASSWORD_NOMINATE
import com.leijendary.spring.template.iam.entity.Verification.Type.PASSWORD_RESET
import com.leijendary.spring.template.iam.repository.UserCredentialRepository
import com.leijendary.spring.template.iam.repository.UserRepository
import com.leijendary.spring.template.iam.repository.VerificationRepository
import com.leijendary.spring.template.iam.validator.VerificationValidator
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class PasswordService(
    private val passwordEncoder: PasswordEncoder,
    private val userCredentialRepository: UserCredentialRepository,
    private val userRepository: UserRepository,
    private val verificationRepository: VerificationRepository,
    private val verificationValidator: VerificationValidator
) {
    fun nominate(request: PasswordNominateRequest): Next {
        val verificationCode = request.verificationCode!!
        // Validate if the verification code exists first
        val verification = verificationValidator.validateByCode(verificationCode, PASSWORD_NOMINATE)
        val user = verification.user!!
        val field = verification.field!!
        val credential = user
            .credentials
            .firstOrNull { it.type == field }
            ?: throw InvalidCredentialException()
        val password = request.password!!

        transactional {
            updatePassword(credential, password)

            verificationRepository.delete(verification)
        }

        return Next(AUTHENTICATE.value)
    }

    fun change(request: PasswordChangeRequest): Next {
        val user = transactional(readOnly = true) {
            userRepository.findByIdOrThrow(userIdOrThrow)
        }!!
        val field = request.field!!
        val credential = user
            .credentials
            .firstOrNull { it.type == field }
            ?: throw InvalidCredentialException()
        val currentPassword = request.currentPassword!!

        // Validate if the current password matches.
        if (!passwordEncoder.matches(currentPassword, credential.password)) {
            throw InvalidCredentialException()
        }

        val password = request.password!!

        updatePassword(credential, password)

        return Next(AUTHENTICATE.value)
    }

    fun reset(request: PasswordResetRequest): Next {
        val field = request.field!!
        val value = request.value!!
        val verificationCode = request.verificationCode!!
        // Validate if the verification code exists first.
        val verification = verificationValidator.validateByField(field, value, verificationCode, PASSWORD_RESET)
        val credential = transactional(readOnly = true) {
            userCredentialRepository.findFirstByUsernameAndTypeAndUserDeletedAtIsNullOrThrow(value, field)
        }!!
        val password = request.password!!

        transactional {
            updatePassword(credential, password)

            verificationRepository.delete(verification)
        }

        return Next(AUTHENTICATE.value)
    }

    private fun updatePassword(credential: UserCredential, password: String) {
        credential.password = passwordEncoder.encode(password)

        userCredentialRepository.save(credential)
    }
}

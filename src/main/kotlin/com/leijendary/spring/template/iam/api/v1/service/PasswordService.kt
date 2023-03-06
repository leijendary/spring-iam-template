package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.model.NextCode
import com.leijendary.spring.template.iam.api.v1.model.NextCode.Type.AUTHENTICATE
import com.leijendary.spring.template.iam.api.v1.model.PasswordChangeRequest
import com.leijendary.spring.template.iam.api.v1.model.PasswordNominateRequest
import com.leijendary.spring.template.iam.api.v1.model.PasswordResetRequest
import com.leijendary.spring.template.iam.core.exception.InvalidCredentialException
import com.leijendary.spring.template.iam.core.extension.transactional
import com.leijendary.spring.template.iam.core.util.RequestContext.userIdOrThrow
import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.entity.Verification.Type.PASSWORD_NOMINATE
import com.leijendary.spring.template.iam.entity.Verification.Type.PASSWORD_RESET
import com.leijendary.spring.template.iam.repository.UserCredentialRepository
import com.leijendary.spring.template.iam.repository.UserRepository
import com.leijendary.spring.template.iam.validator.VerificationValidator
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class PasswordService(
    private val passwordEncoder: PasswordEncoder,
    private val userCredentialRepository: UserCredentialRepository,
    private val userRepository: UserRepository,
    private val verificationValidator: VerificationValidator
) {
    fun nominate(request: PasswordNominateRequest): NextCode {
        val verificationCode = request.verificationCode!!
        val verification = verificationValidator.validateByCode(verificationCode, PASSWORD_NOMINATE)
        val user = verification.user!!
        val field = verification.field!!
        val credential = user
            .credentials
            .firstOrNull { it.type == field }
            ?: throw InvalidCredentialException()
        val password = request.password!!

        updatePassword(credential, password)

        return NextCode(AUTHENTICATE.value)
    }

    fun change(request: PasswordChangeRequest): NextCode {
        val user = userIdOrThrow.let {
            transactional(readOnly = true) {
                userRepository.findByIdOrThrow(it)
            }!!
        }
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

        return NextCode(AUTHENTICATE.value)
    }

    fun reset(request: PasswordResetRequest): NextCode {
        val field = request.field!!
        val value = request.value!!
        val verificationCode = request.verificationCode!!
        val deviceId = request.deviceId!!

        // Validate if the verification code exists first.
        verificationValidator.validateByField(field, value, verificationCode, deviceId, PASSWORD_RESET)

        val credential = transactional(readOnly = true) {
            userCredentialRepository.findFirstByUsernameAndTypeAndUserDeletedAtIsNullOrThrow(value, field)
        }!!
        val password = request.password!!

        updatePassword(credential, password)

        return NextCode(AUTHENTICATE.value)
    }

    private fun updatePassword(credential: UserCredential, password: String) {
        credential.password = passwordEncoder.encode(password)

        userCredentialRepository.save(credential)
    }
}

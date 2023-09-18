package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.mapper.TokenMapper
import com.leijendary.spring.template.iam.api.v1.model.*
import com.leijendary.spring.template.iam.api.v1.model.NextResponse.Type.AUTHENTICATE
import com.leijendary.spring.template.iam.core.datasource.transactional
import com.leijendary.spring.template.iam.core.exception.InvalidCredentialException
import com.leijendary.spring.template.iam.core.util.RequestContext.userIdOrThrow
import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.entity.Verification.Type.PASSWORD_NOMINATE
import com.leijendary.spring.template.iam.entity.Verification.Type.PASSWORD_RESET
import com.leijendary.spring.template.iam.manager.AuthorizationManager
import com.leijendary.spring.template.iam.repository.UserCredentialRepository
import com.leijendary.spring.template.iam.repository.UserRepository
import com.leijendary.spring.template.iam.repository.VerificationRepository
import com.leijendary.spring.template.iam.validator.VerificationValidator
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class PasswordService(
    private val authorizationManager: AuthorizationManager,
    private val passwordEncoder: PasswordEncoder,
    private val userCredentialRepository: UserCredentialRepository,
    private val userRepository: UserRepository,
    private val verificationRepository: VerificationRepository,
    private val verificationValidator: VerificationValidator
) {
    fun nominate(request: PasswordNominateRequest): TokenResponse {
        val verificationCode = request.verificationCode!!
        // Validate if the verification code exists first
        val verification = verificationValidator.validateByCode(verificationCode, PASSWORD_NOMINATE)
        val credential = verification.user!!.credentials.firstOrNull { it.type == verification.field!! }
            ?: throw InvalidCredentialException()
        val password = request.password!!

        transactional {
            updatePassword(credential, password)

            verificationRepository.delete(verification)
        }

        val auth = authorizationManager.authorize(credential.user, credential.username, credential.type)

        return TokenMapper.INSTANCE.toResponse(auth)
    }

    fun change(request: PasswordChangeRequest): NextResponse {
        val user = userRepository.findCachedByIdOrThrow(userIdOrThrow)
        val credential = user.credentials.firstOrNull { it.type == request.field!! }
            ?: throw InvalidCredentialException()
        val currentPassword = request.currentPassword!!

        // Validate if the current password matches.
        if (!passwordEncoder.matches(currentPassword, credential.password)) {
            throw InvalidCredentialException()
        }

        val password = request.password!!

        updatePassword(credential, password)

        return NextResponse(AUTHENTICATE.value)
    }

    fun reset(request: PasswordResetRequest): TokenResponse {
        val field = request.field!!
        val value = request.value!!
        val verificationCode = request.verificationCode!!
        // Validate if the verification code exists first.
        val verification = verificationValidator.validateByField(field, value, verificationCode, PASSWORD_RESET)
        val credential = userCredentialRepository
            .findFirstByUsernameIgnoreCaseAndTypeAndUserDeletedAtIsNullOrThrow(value, field)
        val password = request.password!!

        transactional {
            updatePassword(credential, password)

            verificationRepository.delete(verification)
        }

        val auth = authorizationManager.authorize(credential.user, credential.username, credential.type)

        return TokenMapper.INSTANCE.toResponse(auth)
    }

    private fun updatePassword(credential: UserCredential, password: String) {
        credential.password = passwordEncoder.encode(password)

        userCredentialRepository.save(credential)
    }
}

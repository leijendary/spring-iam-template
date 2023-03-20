package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.mapper.ProfileMapper
import com.leijendary.spring.template.iam.api.v1.model.*
import com.leijendary.spring.template.iam.api.v1.model.Next.Type.AUTHENTICATE
import com.leijendary.spring.template.iam.core.extension.transactional
import com.leijendary.spring.template.iam.core.storage.S3Storage
import com.leijendary.spring.template.iam.core.util.RequestContext.userIdOrThrow
import com.leijendary.spring.template.iam.repository.UserCredentialRepository
import com.leijendary.spring.template.iam.repository.UserRepository
import com.leijendary.spring.template.iam.repository.VerificationRepository
import com.leijendary.spring.template.iam.validator.VerificationValidator
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProfileService(
    private val s3Storage: S3Storage,
    private val userCredentialRepository: UserCredentialRepository,
    private val userRepository: UserRepository,
    private val verificationRepository: VerificationRepository,
    private val verificationValidator: VerificationValidator
) {
    companion object {
        private const val CACHE_NAME = "profile:v1"
        private val MAPPER = ProfileMapper.INSTANCE
    }

    @Cacheable(value = [CACHE_NAME], key = "#id")
    fun detail(id: UUID): ProfileResponse {
        val user = transactional(readOnly = true) {
            userRepository.findByIdOrThrow(id)
        }!!

        return MAPPER.toResponse(user, s3Storage)
    }

    @CachePut(value = [CACHE_NAME], key = "#id")
    fun update(id: UUID, request: ProfileRequest): ProfileResponse {
        val user = transactional {
            userRepository
                .findByIdOrThrow(id)
                .let {
                    MAPPER.update(request, it)

                    userRepository.save(it)
                }
        }!!

        return MAPPER.toResponse(user, s3Storage)
    }

    fun username(request: UpdateUsernameRequest): Next {
        val credentialType = request.credentialType.value
        val username = request.username
        val verificationCode = request.verificationCode!!
        val verificationType = request.verificationType
        // Validate the verification code first
        val verification = verificationValidator.validateByField(
            credentialType,
            username,
            verificationCode,
            verificationType
        )
        val user = transactional(readOnly = true) {
            userRepository.findByIdOrThrow(userIdOrThrow)
        }!!
        val credential = user
            .credentials
            .firstOrNull { it.type == credentialType }

        when (request) {
            is UpdateEmailRequest -> MAPPER.update(request, user)
            is UpdatePhoneRequest -> MAPPER.update(request, user)
        }

        user.setVerified(credentialType)

        transactional {
            userRepository.save(user)

            credential?.let {
                it.username = username

                userCredentialRepository.save(credential)
            }

            verificationRepository.delete(verification)
        }

        return Next(AUTHENTICATE.value)
    }
}

package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.mapper.ProfileMapper
import com.leijendary.spring.template.iam.api.v1.model.*
import com.leijendary.spring.template.iam.api.v1.model.Next.Type.AUTHENTICATE
import com.leijendary.spring.template.iam.core.datasource.transactional
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
    private val userCredentialRepository: UserCredentialRepository,
    private val userRepository: UserRepository,
    private val verificationRepository: VerificationRepository,
    private val verificationValidator: VerificationValidator
) {
    companion object {
        private const val CACHE_NAME = "profile:v1"
    }

    @Cacheable(value = [CACHE_NAME], key = "#id")
    fun detail(id: UUID): ProfileResponse {
        val user = userRepository.findByIdOrThrow(id)

        return ProfileMapper.INSTANCE.toResponse(user)
    }

    @CachePut(value = [CACHE_NAME], key = "#id")
    fun update(id: UUID, request: ProfileRequest): ProfileResponse {
        val user = transactional {
            userRepository
                .findByIdOrThrow(id)
                .let {
                    ProfileMapper.INSTANCE.update(request, it)

                    userRepository.save(it)
                }
        }!!

        return ProfileMapper.INSTANCE.toResponse(user)
    }

    fun username(request: UpdateUsernameRequest): Next {
        val credentialType = request.credentialType
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
        val user = userRepository.findByIdOrThrow(userIdOrThrow)
        val credential = user
            .credentials
            .firstOrNull { it.type == credentialType }

        when (request) {
            is UpdateEmailRequest -> ProfileMapper.INSTANCE.update(request, user)
            is UpdatePhoneRequest -> ProfileMapper.INSTANCE.update(request, user)
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

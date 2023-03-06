package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.mapper.ProfileMapper
import com.leijendary.spring.template.iam.api.v1.model.*
import com.leijendary.spring.template.iam.core.config.properties.VerificationProperties
import com.leijendary.spring.template.iam.core.extension.transactional
import com.leijendary.spring.template.iam.core.storage.S3Storage
import com.leijendary.spring.template.iam.core.util.RequestContext.userIdOrSystem
import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.entity.Verification
import com.leijendary.spring.template.iam.event.CredentialEvent
import com.leijendary.spring.template.iam.generator.CodeGenerationStrategy
import com.leijendary.spring.template.iam.repository.UserCredentialRepository
import com.leijendary.spring.template.iam.repository.UserRepository
import com.leijendary.spring.template.iam.repository.VerificationRepository
import com.leijendary.spring.template.iam.util.VerificationType
import com.leijendary.spring.template.iam.validator.VerificationValidator
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProfileService(
    private val credentialEvent: CredentialEvent,
    private val s3Storage: S3Storage,
    private val userCredentialRepository: UserCredentialRepository,
    private val userRepository: UserRepository,
    private val verificationProperties: VerificationProperties,
    private val verificationRepository: VerificationRepository,
    private val verificationValidator: VerificationValidator
) {
    companion object {
        private const val CACHE_NAME = "profile:v1"
        private val MAPPER = ProfileMapper.INSTANCE
    }

    @Cacheable(value = [CACHE_NAME], key = "#userId")
    fun detail(userId: String): ProfileResponse {
        val id = UUID.fromString(userId)
        val user = transactional(readOnly = true) {
            userRepository.findByIdOrThrow(id)
        }!!

        return MAPPER.toResponse(user, s3Storage)
    }

    @CachePut(value = [CACHE_NAME], key = "#userId")
    fun update(userId: String, request: ProfileRequest): ProfileResponse {
        val id = UUID.fromString(userId)
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

    fun email(request: EmailUpdateRequest): NextCode {
        val field = UserCredential.Type.EMAIL.value
        val type = VerificationType.EMAIL_VERIFY
        val deviceId = request.deviceId!!
        val email = request.email!!

        return username(field, type, deviceId, email)
    }

    fun emailVerify(request: VerifyRequest): NextCode {
        val type = VerificationType.EMAIL_VERIFY

        return verify(request, type)
    }

    fun phone(request: PhoneUpdateRequest): NextCode {
        val field = UserCredential.Type.PHONE.value
        val type = VerificationType.PHONE_VERIFY
        val deviceId = request.deviceId!!
        val countryCode = request.countryCode!!
        val phone = request.phone!!

        return username(field, type, deviceId, phone, countryCode)
    }

    fun phoneVerify(request: VerifyRequest): NextCode {
        val type = VerificationType.PHONE_VERIFY

        return verify(request, type)
    }

    private fun username(
        field: String,
        type: String,
        deviceId: String,
        username: String,
        countryCode: String? = null
    ): NextCode {
        val id = UUID.fromString(userIdOrSystem)
        val user = transactional(readOnly = true) {
            userRepository.findByIdOrThrow(id)
        }!!
        val credential = user
            .credentials
            .firstOrNull { it.type == field }
        val generator = CodeGenerationStrategy.fromField(field)
        val code = generator.generate()
        val verification = Verification().apply {
            this.user = user
            this.code = code
            this.field = field
            this.deviceId = deviceId
            this.type = type
            expiresAt = verificationProperties.computeExpiration()
        }

        transactional {
            user.apply {
                if (!countryCode.isNullOrBlank()) {
                    this.countryCode = countryCode
                }

                setUsername(field, username)
                setVerified(field, false)
            }

            credential?.let {
                it.username = username

                userCredentialRepository.save(credential)
            }

            userRepository.save(user)
            verificationRepository.save(verification)
        }

        credentialEvent.verify(verification)

        return NextCode(type)
    }

    private fun verify(request: VerifyRequest, type: String): NextCode {
        val verification = verificationValidator.validate(request.code!!, type, request.deviceId!!)
        val field = verification.field
        val user = verification.user!!
        user.setVerified(field)

        userRepository.save(user)

        return NextCode(VerificationType.AUTHENTICATE)
    }
}

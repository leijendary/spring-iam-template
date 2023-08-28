package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.mapper.TokenMapper
import com.leijendary.spring.template.iam.api.v1.mapper.UserMapper
import com.leijendary.spring.template.iam.api.v1.model.RegisterEmailRequest
import com.leijendary.spring.template.iam.api.v1.model.RegisterPhoneRequest
import com.leijendary.spring.template.iam.api.v1.model.RegisterRequest
import com.leijendary.spring.template.iam.api.v1.model.TokenResponse
import com.leijendary.spring.template.iam.core.datasource.transactional
import com.leijendary.spring.template.iam.core.util.RequestContext.locale
import com.leijendary.spring.template.iam.entity.Account
import com.leijendary.spring.template.iam.entity.Role
import com.leijendary.spring.template.iam.entity.User
import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.entity.Verification.Type.REGISTRATION
import com.leijendary.spring.template.iam.manager.AuthorizationManager
import com.leijendary.spring.template.iam.message.NotificationMessageProducer
import com.leijendary.spring.template.iam.message.UserMessageProducer
import com.leijendary.spring.template.iam.repository.RoleRepository
import com.leijendary.spring.template.iam.repository.UserRepository
import com.leijendary.spring.template.iam.repository.VerificationRepository
import com.leijendary.spring.template.iam.validator.VerificationValidator
import org.springframework.context.MessageSource
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class RegisterService(
    private val authorizationManager: AuthorizationManager,
    private val messageSource: MessageSource,
    private val notificationMessageProducer: NotificationMessageProducer,
    private val passwordEncoder: PasswordEncoder,
    private val roleRepository: RoleRepository,
    private val userMessageProducer: UserMessageProducer,
    private val userRepository: UserRepository,
    private val verificationRepository: VerificationRepository,
    private val verificationValidator: VerificationValidator
) {
    fun register(request: RegisterRequest): TokenResponse {
        val credentialType = request.credentialType
        val username = request.username
        val verificationCode = request.verificationCode!!
        // Validate if the verification code exists first.
        val verification = verificationValidator
            .validateByField(credentialType, username, verificationCode, REGISTRATION)
        val account = Account().apply {
            type = Account.Type.CUSTOMER
            status = Account.Status.ACTIVE
        }
        val role = roleRepository.findCachedByNameOrThrow(Role.Default.CUSTOMER.value)
        val user = when (request) {
            is RegisterEmailRequest -> UserMapper.INSTANCE.toEntity(request)
            is RegisterPhoneRequest -> UserMapper.INSTANCE.toEntity(request)
        }.apply {
            this.account = account
            this.role = role
            this.status = User.Status.ACTIVE

            setVerified(credentialType)
        }
        val credential = UserCredential().apply {
            this.user = user
            this.username = username
            this.type = credentialType
            this.password = passwordEncoder.encode(request.password!!)
        }

        user.credentials.add(credential)

        transactional {
            userRepository.saveAndCache(user)
            verificationRepository.delete(verification)
        }

        userMessageProducer.registered(user)

        val title = messageSource.getMessage("notification.push.registration.title", emptyArray(), locale)
        val body = messageSource.getMessage("notification.push.registration.body", emptyArray(), locale)

        notificationMessageProducer.push(user.id!!, title, body)

        val auth = authorizationManager.authorize(user, username, credentialType)

        return TokenMapper.INSTANCE.toResponse(auth)
    }
}

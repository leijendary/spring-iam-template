package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.mapper.UserMapper
import com.leijendary.spring.template.iam.api.v1.model.Next
import com.leijendary.spring.template.iam.api.v1.model.Next.Type.AUTHENTICATE
import com.leijendary.spring.template.iam.api.v1.model.RegisterEmailRequest
import com.leijendary.spring.template.iam.api.v1.model.RegisterPhoneRequest
import com.leijendary.spring.template.iam.api.v1.model.RegisterRequest
import com.leijendary.spring.template.iam.core.extension.transactional
import com.leijendary.spring.template.iam.core.util.RequestContext.locale
import com.leijendary.spring.template.iam.entity.Account
import com.leijendary.spring.template.iam.entity.Role
import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.entity.Verification.Type.REGISTRATION
import com.leijendary.spring.template.iam.message.NotificationProducer
import com.leijendary.spring.template.iam.model.PushMessage
import com.leijendary.spring.template.iam.repository.RoleRepository
import com.leijendary.spring.template.iam.repository.UserRepository
import com.leijendary.spring.template.iam.repository.VerificationRepository
import com.leijendary.spring.template.iam.util.Status.ACTIVE
import com.leijendary.spring.template.iam.validator.VerificationValidator
import org.springframework.context.MessageSource
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class RegisterService(
    private val messageSource: MessageSource,
    private val notificationProducer: NotificationProducer,
    private val passwordEncoder: PasswordEncoder,
    private val roleRepository: RoleRepository,
    private val userRepository: UserRepository,
    private val verificationRepository: VerificationRepository,
    private val verificationValidator: VerificationValidator
) {
    companion object {
        private val MAPPER = UserMapper.INSTANCE
    }

    fun register(request: RegisterRequest): Next {
        val credentialType = request.credentialType.value
        val username = request.username
        val verificationCode = request.verificationCode!!
        // Validate if the verification code exists first.
        val verification =
            verificationValidator.validateByField(credentialType, username, verificationCode, REGISTRATION)
        val account = Account().apply {
            type = Account.Type.CUSTOMER.value
            status = ACTIVE
        }
        val role = transactional(readOnly = true) {
            roleRepository.findFirstByNameOrThrow(Role.Default.CUSTOMER.value)
        }!!
        val user = when (request) {
            is RegisterEmailRequest -> MAPPER.toEntity(request)
            is RegisterPhoneRequest -> MAPPER.toEntity(request)
        }.apply {
            this.account = account
            this.role = role
            this.status = ACTIVE

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
            userRepository.save(user)
            verificationRepository.delete(verification)
        }

        val userId = user.id!!
        val title = messageSource.getMessage("notification.push.registration.title", emptyArray(), locale)
        val body = messageSource.getMessage("notification.push.registration.body", emptyArray(), locale)
        val pushMessage = PushMessage(userId, title, body)

        notificationProducer.push(pushMessage)

        return Next(AUTHENTICATE.value)
    }
}

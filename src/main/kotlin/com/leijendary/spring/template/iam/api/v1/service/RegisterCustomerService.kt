package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.mapper.UserMapper
import com.leijendary.spring.template.iam.api.v1.model.NextCode
import com.leijendary.spring.template.iam.api.v1.model.RegisterCustomerEmailRequest
import com.leijendary.spring.template.iam.api.v1.model.RegisterCustomerFullRequest
import com.leijendary.spring.template.iam.api.v1.model.RegisterCustomerMobileRequest
import com.leijendary.spring.template.iam.core.config.properties.VerificationProperties
import com.leijendary.spring.template.iam.core.exception.ResourceNotFoundException
import com.leijendary.spring.template.iam.core.extension.transactional
import com.leijendary.spring.template.iam.core.util.RequestContext.now
import com.leijendary.spring.template.iam.entity.Account
import com.leijendary.spring.template.iam.entity.Role
import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.entity.Verification
import com.leijendary.spring.template.iam.event.CredentialEvent
import com.leijendary.spring.template.iam.generator.CodeGenerationStrategy
import com.leijendary.spring.template.iam.repository.RoleRepository
import com.leijendary.spring.template.iam.repository.UserRepository
import com.leijendary.spring.template.iam.repository.VerificationRepository
import com.leijendary.spring.template.iam.util.Status
import com.leijendary.spring.template.iam.util.VerificationType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class RegisterCustomerService(
    private val credentialEvent: CredentialEvent,
    private val passwordEncoder: PasswordEncoder,
    private val roleRepository: RoleRepository,
    private val userRepository: UserRepository,
    private val verificationProperties: VerificationProperties,
    private val verificationRepository: VerificationRepository
) {
    companion object {
        private val MAPPER = UserMapper.INSTANCE
        private val SOURCE_ROLE = listOf("data", "Role", "name")
    }

    fun mobile(request: RegisterCustomerMobileRequest): NextCode {
        val account = Account().apply {
            type = Account.Type.CUSTOMER.value
            status = Status.ACTIVE
        }
        val roleName = Role.Defaults.CUSTOMER.value
        val role = roleRepository
            .findFirstByName(roleName)
            ?: throw ResourceNotFoundException(SOURCE_ROLE, roleName)
        val user = MAPPER.from(request).apply {
            this.account = account
            this.role = role
            this.status = Status.FOR_VERIFICATION
        }
        val generator = CodeGenerationStrategy.OTP_STRATEGY
        val code = generator.generate()
        val verification = Verification().apply {
            this.user = user
            this.code = code
            this.field = UserCredential.Type.PHONE.value
            deviceId = request.deviceId!!
            type = VerificationType.REGISTRATION
            expiry = now.plus(verificationProperties.expiry)
        }

        transactional {
            userRepository.save(user)
            verificationRepository.save(verification)
        }

        credentialEvent.verify(verification)

        return NextCode(VerificationType.VERIFICATION, null)
    }

    fun email(request: RegisterCustomerEmailRequest): NextCode {
        val account = Account().apply {
            type = Account.Type.CUSTOMER.value
            status = Status.ACTIVE
        }
        val roleName = Role.Defaults.CUSTOMER.value
        val role = roleRepository
            .findFirstByName(roleName)
            ?: throw ResourceNotFoundException(SOURCE_ROLE, roleName)
        val user = MAPPER.from(request).apply {
            this.account = account
            this.role = role
            this.status = Status.FOR_VERIFICATION
        }
        val generator = CodeGenerationStrategy.UUID_STRATEGY
        val code = generator.generate()
        val verification = Verification().apply {
            this.user = user
            this.code = code
            this.field = UserCredential.Type.EMAIL.value
            deviceId = request.deviceId!!
            type = VerificationType.REGISTRATION
            expiry = now.plus(verificationProperties.expiry)
        }

        transactional {
            userRepository.save(user)
            verificationRepository.save(verification)
        }

        credentialEvent.verify(verification)

        return NextCode(VerificationType.VERIFICATION, null)
    }

    fun full(request: RegisterCustomerFullRequest): NextCode {
        val account = Account().apply {
            type = Account.Type.CUSTOMER.value
            status = Status.ACTIVE
        }
        val roleName = Role.Defaults.CUSTOMER.value
        val role = roleRepository
            .findFirstByName(roleName)
            ?: throw ResourceNotFoundException(SOURCE_ROLE, roleName)
        val user = MAPPER.from(request).apply {
            this.account = account
            this.role = role
            this.status = Status.FOR_VERIFICATION
        }
        val field = request.preferredUsername!!
        val username = user.getUsername(field)
        val password = passwordEncoder.encode(request.password!!)

        UserCredential()
            .apply {
                this.user = user
                this.username = username
                this.type = field
                this.password = password
            }
            .run {
                user.credentials.add(this)
            }

        val generator = CodeGenerationStrategy.fromField(field)
        val code = generator.generate()
        val verification = Verification().apply {
            this.user = user
            this.code = code
            this.field = field
            deviceId = request.deviceId!!
            type = VerificationType.REGISTRATION
            expiry = now.plus(verificationProperties.expiry)
        }

        transactional {
            userRepository.save(user)
            verificationRepository.save(verification)
        }

        credentialEvent.verify(verification)

        return NextCode(VerificationType.VERIFICATION, null)
    }
}

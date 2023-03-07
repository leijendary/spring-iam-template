package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.mapper.UserMapper
import com.leijendary.spring.template.iam.api.v1.model.Next
import com.leijendary.spring.template.iam.api.v1.model.Next.Type.AUTHENTICATE
import com.leijendary.spring.template.iam.api.v1.model.RegisterEmailRequest
import com.leijendary.spring.template.iam.api.v1.model.RegisterPhoneRequest
import com.leijendary.spring.template.iam.api.v1.model.RegisterRequest
import com.leijendary.spring.template.iam.core.extension.transactional
import com.leijendary.spring.template.iam.entity.Account
import com.leijendary.spring.template.iam.entity.Role
import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.entity.Verification.Type.REGISTRATION
import com.leijendary.spring.template.iam.repository.RoleRepository
import com.leijendary.spring.template.iam.repository.UserRepository
import com.leijendary.spring.template.iam.repository.VerificationRepository
import com.leijendary.spring.template.iam.util.Status.ACTIVE
import com.leijendary.spring.template.iam.validator.VerificationValidator
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class RegisterService(
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
        val deviceId = request.deviceId!!
        // Validate if the verification code exists first.
        val verification = verificationValidator.validateByField(
            credentialType,
            username,
            verificationCode,
            deviceId,
            REGISTRATION
        )
        val account = Account().apply {
            type = Account.Type.CUSTOMER.value
            status = ACTIVE
        }
        val role = transactional(readOnly = true) {
            roleRepository.findFirstByNameOrThrow(Role.Default.CUSTOMER.value)
        }!!
        val user = when (request) {
            is RegisterEmailRequest -> MAPPER.from(request)
            is RegisterPhoneRequest -> MAPPER.from(request)
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

        return Next(AUTHENTICATE.value)
    }
}

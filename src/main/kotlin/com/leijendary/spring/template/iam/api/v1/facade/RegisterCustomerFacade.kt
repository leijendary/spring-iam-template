package com.leijendary.spring.template.iam.api.v1.facade

import com.leijendary.spring.template.iam.core.config.properties.VerificationProperties
import com.leijendary.spring.template.iam.core.extension.transactional
import com.leijendary.spring.template.iam.entity.*
import com.leijendary.spring.template.iam.event.CredentialEvent
import com.leijendary.spring.template.iam.generator.CodeGenerationStrategy
import com.leijendary.spring.template.iam.repository.RoleRepository
import com.leijendary.spring.template.iam.repository.UserRepository
import com.leijendary.spring.template.iam.repository.VerificationRepository
import com.leijendary.spring.template.iam.util.Status
import com.leijendary.spring.template.iam.util.VerificationType
import org.springframework.stereotype.Component

@Component
class RegisterCustomerFacade(
    private val credentialEvent: CredentialEvent,
    private val roleRepository: RoleRepository,
    private val userRepository: UserRepository,
    private val verificationProperties: VerificationProperties,
    private val verificationRepository: VerificationRepository,
) {
    fun register(user: User, credentialType: UserCredential.Type, deviceId: String, password: String?) {
        val account = Account().apply {
            this.type = Account.Type.CUSTOMER.value
            status = Status.ACTIVE
        }
        val roleName = Role.Defaults.CUSTOMER.value
        val role = transactional(readOnly = true) {
            roleRepository.findFirstByNameOrThrow(roleName)
        }!!

        user.apply {
            this.account = account
            this.role = role
            this.status = Status.FOR_VERIFICATION
        }

        val field = credentialType.value
        val username = user.getUsername(field)

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
            this.deviceId = deviceId
            this.type = VerificationType.REGISTRATION
            expiresAt = verificationProperties.computeExpiration()
        }

        transactional {
            userRepository.save(user)
            verificationRepository.save(verification)
        }

        credentialEvent.verify(verification)
    }
}

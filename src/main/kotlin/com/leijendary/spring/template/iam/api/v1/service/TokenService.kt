package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.mapper.TokenMapper
import com.leijendary.spring.template.iam.api.v1.mapper.UserMapper
import com.leijendary.spring.template.iam.api.v1.model.*
import com.leijendary.spring.template.iam.core.datasource.transactional
import com.leijendary.spring.template.iam.core.exception.InvalidCredentialException
import com.leijendary.spring.template.iam.core.exception.StatusException
import com.leijendary.spring.template.iam.core.util.RequestContext.now
import com.leijendary.spring.template.iam.entity.Account
import com.leijendary.spring.template.iam.entity.Role
import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.entity.UserCredential.Type.EMAIL
import com.leijendary.spring.template.iam.entity.UserSocial
import com.leijendary.spring.template.iam.entity.UserSocial.Provider
import com.leijendary.spring.template.iam.manager.AuthorizationManager
import com.leijendary.spring.template.iam.model.SocialResult
import com.leijendary.spring.template.iam.registry.SocialVerificationRegistry
import com.leijendary.spring.template.iam.repository.RoleRepository
import com.leijendary.spring.template.iam.repository.UserCredentialRepository
import com.leijendary.spring.template.iam.repository.UserRepository
import com.leijendary.spring.template.iam.repository.UserSocialRepository
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

private val socialSource = listOf("body", "provider")

@Service
class TokenService(
    private val authorizationManager: AuthorizationManager,
    private val passwordEncoder: PasswordEncoder,
    private val roleRepository: RoleRepository,
    private val socialVerificationRegistry: SocialVerificationRegistry,
    private val userCredentialRepository: UserCredentialRepository,
    private val userRepository: UserRepository,
    private val userSocialRepository: UserSocialRepository,
) {
    fun create(request: TokenRequest): TokenResponse {
        val username = request.username!!
        val password = request.password!!
        val credential = userCredentialRepository.findFirstByUsernameIgnoreCaseAndUserDeletedAtIsNullOrThrow(username)
        val encoded = credential.password

        if (!passwordEncoder.matches(password, encoded)) {
            throw InvalidCredentialException()
        }

        // Try to check if the user's password encoding needs to be upgraded
        if (passwordEncoder.upgradeEncoding(encoded)) {
            credential.password = passwordEncoder.encode(password)
        }

        credential.lastUsedAt = now

        userCredentialRepository.save(credential)

        val auth = authorizationManager.authorize(credential.user, username, credential.type)

        return TokenMapper.INSTANCE.toResponse(auth)
    }

    fun refresh(request: TokenRefreshRequest): TokenResponse {
        val refreshToken = request.refreshToken!!
        val auth = authorizationManager.refresh(refreshToken)

        return TokenMapper.INSTANCE.toResponse(auth)
    }

    fun revoke(request: TokenRevokeRequest) {
        val accessToken = request.accessToken!!

        authorizationManager.revoke(accessToken)
    }

    fun social(request: SocialRequest): TokenResponse {
        val provider = request.provider!!
        val result = socialVerificationRegistry.using(provider) { verify(request.token!!) }!!
        val userSocial = userSocialRepository.findByIdAndUserDeletedAtIsNull(result.id)
            ?: createSocial(request, result, provider)
        val auth = authorizationManager.authorize(userSocial.user, result.email, EMAIL)

        return TokenMapper.INSTANCE.toResponse(auth)
    }

    private fun createSocial(request: SocialRequest, result: SocialResult, provider: Provider): UserSocial {
        var credential = userCredentialRepository.findFirstByUsernameIgnoreCaseAndUserDeletedAtIsNull(result.email)
        var user = credential?.user

        if (user != null) {
            val hasProvider = userSocialRepository.existsByUserIdAndProvider(user.id!!, provider)

            // The user already has the same social provider attached to his/her account.
            if (hasProvider) {
                throw StatusException(socialSource, "access.user.social.exists", BAD_REQUEST, arrayOf(provider))
            }
        } else {
            val account = Account().apply {
                type = Account.Type.CUSTOMER
                status = Account.Status.ACTIVE
            }
            val role = roleRepository.findCachedByNameOrThrow(Role.Default.CUSTOMER.value)
            user = UserMapper.INSTANCE.toEntity(result).apply {
                // Replace null or blank first and last names from the request. This is applicable to
                // apple since they give the frontend the first name and last name of the user.
                this.firstName = this.firstName?.ifBlank { request.firstName } ?: request.firstName
                this.lastName = this.lastName?.ifBlank { request.lastName } ?: request.lastName
                this.account = account
                this.role = role
            }
            credential = UserCredential().apply {
                this.user = user
                this.username = result.email
                this.type = EMAIL
            }
            user.credentials.add(credential)

            transactional {
                userRepository.save(user)
                userCredentialRepository.save(credential)
            }
        }

        val social = UserSocial().apply {
            this.id = result.id
            this.user = user
            this.provider = provider
        }

        return userSocialRepository.save(social)
    }
}

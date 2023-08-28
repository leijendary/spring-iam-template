package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.mapper.TokenMapper
import com.leijendary.spring.template.iam.api.v1.mapper.UserMapper
import com.leijendary.spring.template.iam.api.v1.model.*
import com.leijendary.spring.template.iam.core.config.properties.AuthProperties
import com.leijendary.spring.template.iam.core.datasource.transactional
import com.leijendary.spring.template.iam.core.exception.*
import com.leijendary.spring.template.iam.core.security.JwtTools
import com.leijendary.spring.template.iam.core.util.RequestContext.now
import com.leijendary.spring.template.iam.entity.*
import com.leijendary.spring.template.iam.entity.UserCredential.Type.EMAIL
import com.leijendary.spring.template.iam.entity.UserSocial.Provider
import com.leijendary.spring.template.iam.model.SocialResult
import com.leijendary.spring.template.iam.repository.*
import com.leijendary.spring.template.iam.strategy.SocialVerificationStrategy
import com.nimbusds.jose.JOSEException
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class TokenService(
    private val authProperties: AuthProperties,
    private val authRepository: AuthRepository,
    private val jwtTools: JwtTools,
    private val passwordEncoder: PasswordEncoder,
    private val rolePermissionRepository: RolePermissionRepository,
    private val roleRepository: RoleRepository,
    private val userCredentialRepository: UserCredentialRepository,
    private val userRepository: UserRepository,
    private val userSocialRepository: UserSocialRepository,
    socialVerificationStrategies: List<SocialVerificationStrategy>,
) {
    private val socialVerificationStrategy = socialVerificationStrategies.associateBy { it.provider }

    companion object {
        private val accountSource = listOf("data", "Account", "status")
        private val userSource = listOf("data", "User", "status")
        private val accessSource = listOf("body", "accessToken")
        private val refreshSource = listOf("body", "refreshToken")
        private val socialSource = listOf("body", "provider")
    }

    fun create(request: TokenRequest): TokenResponse {
        val username = request.username!!
        val password = request.password!!
        val credential = userCredentialRepository.findFirstByUsernameAndUserDeletedAtIsNullOrThrow(username)
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
        val token = request.token!!
        val provider = request.provider!!
        val result = socialVerificationStrategy[provider]!!.verify(token)
        val userSocial = userSocialRepository.findByIdAndUserDeletedAtIsNull(result.id)
            ?: createSocial(result, provider)
        val auth = authorizationManager.authorize(userSocial.user, result.email, EMAIL)

        return TokenMapper.INSTANCE.toResponse(auth)
    }

    private fun createSocial(socialResult: SocialResult, provider: Provider): UserSocial {
        val email = socialResult.email
        var credential = userCredentialRepository.findFirstByUsernameAndUserDeletedAtIsNull(email)
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
            user = UserMapper.INSTANCE.toEntity(socialResult).apply {
                this.account = account
                this.role = role
            }
            credential = UserCredential().apply {
                this.user = user
                this.username = email
                this.type = EMAIL
            }
            user.credentials.add(credential)

            transactional {
                userRepository.save(user)
                userCredentialRepository.save(credential)
            }
        }

        val social = UserSocial().apply {
            this.id = socialResult.id
            this.user = user
            this.provider = provider
        }

        return userSocialRepository.save(social)
    }
}

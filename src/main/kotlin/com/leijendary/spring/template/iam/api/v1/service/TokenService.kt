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

        val user = credential.user
        val auth = authorize(user, username, credential.type)

        return TokenMapper.INSTANCE.toResponse(auth)
    }

    fun refresh(request: TokenRefreshRequest): TokenResponse {
        val refreshToken = request.refreshToken!!
        val jwt = try {
            jwtTools.parse(refreshToken)
        } catch (exception: JOSEException) {
            throw TokenInvalidSignatureException(refreshSource)
        }

        if (!jwt.isVerified) {
            throw TokenInvalidSignatureException(refreshSource)
        }

        val isExpired = jwt.expirationTime.isBefore(now)

        if (isExpired) {
            val accessTokenId = jwt.accessTokenId!!

            removeByAccessId(accessTokenId)

            throw TokenExpiredException(refreshSource)
        }

        val refreshTokenId = UUID.fromString(jwt.id)
        val auth = authRepository
            .findFirstByRefreshId(refreshTokenId)
            ?.let { authorize(it, it.user) }
            ?: throw ResourceNotFoundException(refreshSource, refreshTokenId)

        return TokenMapper.INSTANCE.toResponse(auth)
    }

    fun revoke(request: TokenRevokeRequest) {
        val accessToken = request.accessToken!!
        val jwt = try {
            jwtTools.parse(accessToken)
        } catch (exception: JOSEException) {
            throw TokenInvalidSignatureException(accessSource)
        }

        if (!jwt.isVerified) {
            throw TokenInvalidSignatureException(accessSource)
        }

        removeByAccessId(jwt.id)
    }

    fun social(request: SocialRequest): TokenResponse {
        val token = request.token!!
        val provider = request.provider!!
        val result = socialVerificationStrategy[provider]!!.verify(token)
        val id = result.id
        val userSocial = userSocialRepository.findByIdAndUserDeletedAtIsNull(id) ?: createSocial(result, provider)
        val user = userSocial.user
        val email = result.email
        val auth = authorize(user, email, EMAIL)

        return TokenMapper.INSTANCE.toResponse(auth)
    }

    private fun createSocial(socialResult: SocialResult, provider: Provider): UserSocial {
        val email = socialResult.email
        val credential = userCredentialRepository.findFirstByUsernameAndUserDeletedAtIsNull(email)
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
            val newUser = UserMapper.INSTANCE.toEntity(socialResult).apply {
                this.account = account
                this.role = role
            }
            val newCredential = UserCredential().apply {
                this.user = newUser
                this.username = email
                this.type = EMAIL
            }
            newUser.credentials.add(newCredential)

            user = transactional {
                userRepository.save(newUser)
                userCredentialRepository.save(newCredential)

                newUser
            }!!
        }

        val social = UserSocial().apply {
            this.id = socialResult.id
            this.user = user
            this.provider = provider
        }

        return userSocialRepository.save(social)
    }

    private fun authorize(user: User, username: String, type: UserCredential.Type): Auth {
        val auth = Auth().apply {
            this.user = user
            this.username = username
            this.type = type
        }

        return authorize(auth, user)
    }

    private fun authorize(auth: Auth, user: User): Auth {
        validateStatus(user.account, user)

        val role = user.role!!
        val scopes = scopes(role)
        val audience = authProperties.issuer
        val jwtSet = jwtTools.create(user.id.toString(), audience, scopes)
        val accessToken = jwtSet.accessToken
        val refreshToken = jwtSet.refreshToken
        val authAccess = AuthAccess().apply {
            this.id = accessToken.id
            this.auth = auth
            this.token = accessToken.value
            this.expiresAt = accessToken.expiration
        }
        val authRefresh = AuthRefresh().apply {
            this.id = refreshToken.id
            this.auth = auth
            this.accessTokenId = accessToken.id
            this.token = refreshToken.value
            this.expiresAt = refreshToken.expiration
        }

        auth.apply {
            this.audience = audience
            this.access = authAccess
            this.refresh = authRefresh
        }

        return transactional { authRepository.save(auth) }!!
    }

    private fun validateStatus(account: Account?, user: User) {
        account?.status?.let {
            if (it != Account.Status.ACTIVE) {
                throw NotActiveException(accountSource, "access.account.inactive")
            }
        }

        if (user.status != User.Status.ACTIVE) {
            throw NotActiveException(userSource, "access.user.inactive")
        }
    }

    private fun scopes(role: Role): Set<String> {
        val permissions = rolePermissionRepository.findAllByRoleId(role.id!!)

        return permissions
            .map { it.permission.value }
            .toSet()
    }

    private fun removeByAccessId(accessTokenId: String) {
        val accessId = UUID.fromString(accessTokenId)
        val auth = authRepository.findFirstByAccessId(accessId) ?: return

        authRepository.delete(auth)
    }
}

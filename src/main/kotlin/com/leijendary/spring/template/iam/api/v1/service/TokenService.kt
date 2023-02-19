package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.mapper.TokenMapper
import com.leijendary.spring.template.iam.api.v1.model.TokenRefreshRequest
import com.leijendary.spring.template.iam.api.v1.model.TokenRequest
import com.leijendary.spring.template.iam.api.v1.model.TokenResponse
import com.leijendary.spring.template.iam.api.v1.model.TokenRevokeRequest
import com.leijendary.spring.template.iam.core.exception.*
import com.leijendary.spring.template.iam.core.extension.transactional
import com.leijendary.spring.template.iam.core.security.JwtTools
import com.leijendary.spring.template.iam.core.util.RequestContext.now
import com.leijendary.spring.template.iam.entity.*
import com.leijendary.spring.template.iam.repository.*
import com.leijendary.spring.template.iam.util.Status
import com.nimbusds.jose.JOSEException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

private val userActiveStatuses = listOf(Status.ACTIVE, Status.INCOMPLETE)

@Service
class TokenService(
    private val authRepository: AuthRepository,
    private val jwtTools: JwtTools,
    private val passwordEncoder: PasswordEncoder,
    private val rolePermissionRepository: RolePermissionRepository,
    private val userCredentialRepository: UserCredentialRepository,
) {
    companion object {
        private val MAPPER = TokenMapper.INSTANCE
        private val ACCOUNT_SOURCE = listOf("data", "Account", "status")
        private val USER_SOURCE = listOf("data", "User", "status")
        private val ACCESS_SOURCE = listOf("body", "accessToken")
        private val REFRESH_SOURCE = listOf("body", "refreshToken")
    }

    fun create(request: TokenRequest): TokenResponse {
        val username = request.username!!
        val password = request.password!!
        val credential = transactional(readOnly = true) {
            userCredentialRepository
                .findFirstByUsernameAndUserDeletedAtIsNull(username)
                .orElseThrow { InvalidCredentialException() }
        }

        if (!passwordEncoder.matches(password, credential.password)) {
            throw InvalidCredentialException()
        }

        credential.lastUsedAt = now

        userCredentialRepository.save(credential)

        val user = credential.user!!
        val audience = request.audience!!
        val deviceId = request.deviceId!!
        val auth = Auth()
            .apply {
                this.user = user
                this.username = username
                this.audience = audience
                this.type = credential.type
                this.deviceId = deviceId
            }
            .let {
                authorize(it, user, audience)
            }

        return MAPPER.toResponse(auth)
    }

    fun refresh(request: TokenRefreshRequest): TokenResponse {
        val refreshToken = request.refreshToken!!
        val jwt = try {
            jwtTools.parse(refreshToken)
        } catch (exception: JOSEException) {
            throw TokenInvalidSignatureException(REFRESH_SOURCE)
        }

        if (!jwt.isVerified) {
            throw TokenInvalidSignatureException(REFRESH_SOURCE)
        }

        val isExpired = jwt.expirationTime.isBefore(now)

        if (isExpired) {
            val accessTokenId = jwt.accessTokenId!!

            removeByAccessId(accessTokenId)

            throw TokenExpiredException(REFRESH_SOURCE)
        }

        val refreshTokenId = UUID.fromString(jwt.id)
        val auth = authRepository
            .findFirstByRefreshId(refreshTokenId)
            .orElseThrow { ResourceNotFoundException(REFRESH_SOURCE, refreshTokenId) }
            .let {
                authorize(it, it.user!!, it.audience)
            }

        return MAPPER.toResponse(auth)
    }

    fun revoke(request: TokenRevokeRequest) {
        val accessToken = request.accessToken!!
        val jwt = try {
            jwtTools.parse(accessToken)
        } catch (exception: JOSEException) {
            throw TokenInvalidSignatureException(ACCESS_SOURCE)
        }

        if (!jwt.isVerified) {
            throw TokenInvalidSignatureException(ACCESS_SOURCE)
        }

        removeByAccessId(jwt.id)
    }

    private fun validateStatus(account: Account?, user: User) {
        account?.status?.let {
            if (it != Status.ACTIVE) {
                throw NotActiveException(ACCOUNT_SOURCE, "access.account.inactive")
            }
        }

        if (!userActiveStatuses.contains(user.status)) {
            throw NotActiveException(USER_SOURCE, "access.user.inactive")
        }
    }

    private fun scopes(role: Role): Set<String> {
        val permissions = transactional(readOnly = true) {
            rolePermissionRepository.findAllByRoleId(role.id!!)
        }

        return permissions
            .map { it.permission!!.permission }
            .toSet()
    }

    private fun authorize(auth: Auth, user: User, audience: String): Auth {
        validateStatus(user.account, user)

        val role = user.role!!
        val scopes = scopes(role)
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
            this.access = authAccess
            this.refresh = authRefresh
        }

        return transactional {
            authRepository.save(auth)
        }
    }

    private fun removeByAccessId(accessTokenId: String) {
        val accessId = UUID.fromString(accessTokenId)

        authRepository
            .findFirstByAccessId(accessId)
            .ifPresent {
                authRepository.delete(it)
            }
    }
}

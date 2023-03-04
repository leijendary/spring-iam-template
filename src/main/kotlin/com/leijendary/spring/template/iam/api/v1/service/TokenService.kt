package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.mapper.TokenMapper
import com.leijendary.spring.template.iam.api.v1.mapper.UserDeviceMapper
import com.leijendary.spring.template.iam.api.v1.model.TokenRefreshRequest
import com.leijendary.spring.template.iam.api.v1.model.TokenRequest
import com.leijendary.spring.template.iam.api.v1.model.TokenResponse
import com.leijendary.spring.template.iam.api.v1.model.TokenRevokeRequest
import com.leijendary.spring.template.iam.core.exception.*
import com.leijendary.spring.template.iam.core.extension.transactional
import com.leijendary.spring.template.iam.core.security.JwtTools
import com.leijendary.spring.template.iam.core.util.RequestContext.now
import com.leijendary.spring.template.iam.entity.*
import com.leijendary.spring.template.iam.repository.AuthRepository
import com.leijendary.spring.template.iam.repository.RolePermissionRepository
import com.leijendary.spring.template.iam.repository.UserCredentialRepository
import com.leijendary.spring.template.iam.repository.UserDeviceRepository
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
    private val userDeviceRepository: UserDeviceRepository
) {
    companion object {
        private val TOKEN_MAPPER = TokenMapper.INSTANCE
        private val USER_DEVICE_MAPPER = UserDeviceMapper.INSTANCE
        private val ACCOUNT_SOURCE = listOf("data", "Account", "status")
        private val USER_SOURCE = listOf("data", "User", "status")
        private val ACCESS_SOURCE = listOf("body", "accessToken")
        private val REFRESH_SOURCE = listOf("body", "refreshToken")
    }

    fun create(request: TokenRequest): TokenResponse {
        val username = request.username!!
        val password = request.password!!
        val credential = transactional(readOnly = true) {
            userCredentialRepository.findFirstByUsernameAndUserDeletedAtIsNullOrThrow(username)
        }!!
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

        val user = credential.user!!
        val audience = request.audience!!
        val deviceId = request.deviceId!!

        // Remove previous access token based on the device ID
        transactional { authRepository.deleteByDeviceId(deviceId) }

        val auth = Auth()
            .apply {
                this.user = user
                this.username = username
                this.audience = audience
                this.type = credential.type
                this.deviceId = deviceId
            }
            .let { authorize(it, user, audience) }

        saveDevice(request, user)

        return TOKEN_MAPPER.toResponse(auth)
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
            ?.let { authorize(it, it.user!!, it.audience) }
            ?: throw ResourceNotFoundException(REFRESH_SOURCE, refreshTokenId)

        return TOKEN_MAPPER.toResponse(auth)
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
        }!!

        return permissions
            .map { it.permission!!.value }
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

        return transactional { authRepository.save(auth) }!!
    }

    private fun removeByAccessId(accessTokenId: String) {
        val accessId = UUID.fromString(accessTokenId)
        val auth = transactional(readOnly = true) {
            authRepository.findFirstByAccessId(accessId)
        } ?: return

        authRepository.delete(auth)

        // Also remove the device from the user's possession
        removeDevice(auth.deviceId, auth.user!!.id!!)
    }

    private fun saveDevice(request: TokenRequest, user: User) {
        val token = request.deviceId!!
        var device = transactional(readOnly = true) {
            userDeviceRepository.findFirstByToken(token)
        }

        // Device exists AND the user IDs are the same. Skip the process.
        if (device?.user?.id == user.id) {
            return
        }

        // Device exists and the user ID does not match the current user ID.
        // Delete the old user's device
        if (device != null && device.user?.id != user.id) {
            userDeviceRepository.delete(device)
        }

        // Device does not exist. Then create a device for that user
        device = USER_DEVICE_MAPPER
            .from(request)
            .apply {
                this.user = user
            }

        userDeviceRepository.save(device)
    }

    private fun removeDevice(token: String, userId: UUID) = transactional {
        userDeviceRepository.deleteByTokenAndUserId(token, userId)
    }
}

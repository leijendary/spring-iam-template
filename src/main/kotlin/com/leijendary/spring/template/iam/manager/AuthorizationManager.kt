package com.leijendary.spring.template.iam.manager

class AuthorizationManager {
    fun authorize(user: User, username: String, type: UserCredential.Type): Auth {
        val auth = Auth().apply {
            this.user = user
            this.username = username
            this.type = type
        }

        return authorize(auth, user)
    }

    fun refresh(refreshToken: String): Auth {
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

        return authRepository
            .findFirstByRefreshId(refreshTokenId)
            ?.let { authorize(it, it.user) }
            ?: throw ResourceNotFoundException(refreshSource, refreshTokenId)
    }

    fun revoke(accessToken: String) {
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

    fun removeByAccessId(accessTokenId: String) {
        val accessId = UUID.fromString(accessTokenId)
        val auth = authRepository.findFirstByAccessId(accessId) ?: return

        authRepository.delete(auth)
    }
}

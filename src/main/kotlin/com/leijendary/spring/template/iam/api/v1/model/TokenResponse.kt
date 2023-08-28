package com.leijendary.spring.template.iam.api.v1.model

import java.io.Serializable
import java.time.OffsetDateTime

data class TokenResponse(val access: JwtTokenResponse, val refresh: JwtTokenResponse) : ProfileResponse

data class JwtTokenResponse(val token: String, val expiresAt: OffsetDateTime) : Serializable

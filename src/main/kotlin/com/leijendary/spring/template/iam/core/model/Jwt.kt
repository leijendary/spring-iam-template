package com.leijendary.spring.template.iam.core.model

import java.time.OffsetDateTime
import java.util.*

data class JwtSet(val accessToken: Token, val refreshToken: Token)

data class Token(val id: UUID, val value: String, val expiration: OffsetDateTime)

data class ParsedJwt(
    val id: String,
    val issuer: String,
    val subject: String,
    val audience: List<String>,
    val scopes: Set<String>,
    val expirationTime: OffsetDateTime,
    val issueTime: OffsetDateTime,
    val accessTokenId: String? = null,
    val isVerified: Boolean,
)

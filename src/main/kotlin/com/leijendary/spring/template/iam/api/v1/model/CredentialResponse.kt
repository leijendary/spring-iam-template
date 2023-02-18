package com.leijendary.spring.template.iam.api.v1.model

import java.io.Serializable
import java.time.OffsetDateTime

data class CredentialResponse(
    val username: String,
    val type: String,
    val createdAt: OffsetDateTime,
    val lastUsedAt: OffsetDateTime? = null
) : Serializable

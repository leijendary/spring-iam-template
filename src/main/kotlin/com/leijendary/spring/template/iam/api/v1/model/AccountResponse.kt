package com.leijendary.spring.template.iam.api.v1.model

import java.time.OffsetDateTime
import java.util.*

data class AccountResponse(
    val id: UUID,
    val type: String,
    val status: String,
    val createdAt: OffsetDateTime,
    val createdBy: String,
)

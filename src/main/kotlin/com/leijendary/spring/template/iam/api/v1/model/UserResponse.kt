package com.leijendary.spring.template.iam.api.v1.model

import java.time.OffsetDateTime
import java.util.*

data class UserResponse(
    val id: UUID,
    val account: AccountResponse?,
    val role: RoleResponse,
    val credentials: Set<CredentialResponse>,
    val firstName: String?,
    val middleName: String?,
    val lastName: String?,
    val email: String,
    val emailVerified: Boolean,
    val countryCode: String?,
    val phone: String?,
    val phoneVerified: Boolean,
    var image: String?,
    val status: String,
    val createdAt: OffsetDateTime,
    val createdBy: String,
    val lastModifiedAt: OffsetDateTime,
    val lastModifiedBy: String,
)

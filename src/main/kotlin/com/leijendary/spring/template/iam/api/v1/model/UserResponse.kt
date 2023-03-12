package com.leijendary.spring.template.iam.api.v1.model

import java.io.Serializable
import java.time.OffsetDateTime
import java.util.*

data class UserResponse(
    val id: UUID,
    val account: AccountResponse? = null,
    val role: RoleResponse,
    val credentials: Set<CredentialResponse>,
    val firstName: String? = null,
    val middleName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val emailVerified: Boolean = false,
    val countryCode: String? = null,
    val phone: String? = null,
    val phoneVerified: Boolean = false,
    var image: String? = null,
    val status: String,
    val createdAt: OffsetDateTime,
    val createdBy: String,
    val lastModifiedAt: OffsetDateTime,
    val lastModifiedBy: String,
) : Serializable

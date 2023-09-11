package com.leijendary.spring.template.iam.model

import java.util.*

data class UserMessage(
    val id: UUID,
    val accountId: UUID,
    val roleId: UUID,
    val firstName: String?,
    val middleName: String?,
    val lastName: String?,
    val email: String,
    val emailVerified: Boolean,
    val countryCode: String?,
    val phone: String?,
    val phoneVerified: Boolean,
    val status: String,
)

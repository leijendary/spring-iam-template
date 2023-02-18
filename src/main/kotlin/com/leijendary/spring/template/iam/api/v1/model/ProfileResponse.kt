package com.leijendary.spring.template.iam.api.v1.model

import java.io.Serializable

data class ProfileResponse(
    val firstName: String?,
    val middleName: String?,
    val lastName: String?,
    val email: String?,
    val emailVerified: Boolean = false,
    val countryCode: String?,
    val phone: String?,
    val phoneVerified: Boolean = false,
    val isIncomplete: Boolean = false
) : Serializable

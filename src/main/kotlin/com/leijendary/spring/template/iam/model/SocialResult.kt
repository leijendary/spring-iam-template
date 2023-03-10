package com.leijendary.spring.template.iam.model

data class SocialResult(
    val id: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String,
    val emailVerified: Boolean,
    val picture: String? = null
)

package com.leijendary.spring.template.iam.api.v1.model

import jakarta.validation.constraints.NotBlank

data class TokenRefreshRequest(
    @field:NotBlank(message = "validation.required")
    val refreshToken: String? = null
)

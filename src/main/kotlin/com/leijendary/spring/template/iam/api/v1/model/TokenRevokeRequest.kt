package com.leijendary.spring.template.iam.api.v1.model

import jakarta.validation.constraints.NotBlank

data class TokenRevokeRequest(
    @field:NotBlank(message = "validation.required")
    val accessToken: String? = null,
)

package com.leijendary.spring.template.iam.api.v1.model

import jakarta.validation.constraints.NotBlank

data class TokenRequest(
    @field:NotBlank(message = "validation.required")
    val username: String? = null,

    @field:NotBlank(message = "validation.required")
    val password: String? = null,

    @field:NotBlank(message = "validation.required")
    val audience: String? = null,

    @field:NotBlank(message = "validation.required")
    val deviceId: String? = null,
)

package com.leijendary.spring.template.iam.api.v1.model

import jakarta.validation.constraints.NotBlank

data class PasswordResetRequest(
    @field:NotBlank(message = "validation.required")
    val deviceId: String? = null,

    @field:NotBlank(message = "validation.required")
    val username: String? = null
)
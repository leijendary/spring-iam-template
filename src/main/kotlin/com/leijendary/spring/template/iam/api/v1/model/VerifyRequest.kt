package com.leijendary.spring.template.iam.api.v1.model

import jakarta.validation.constraints.NotBlank

data class VerifyRequest(
    @field:NotBlank(message = "validation.required")
    val code: String? = null,

    @field:NotBlank(message = "validation.required")
    val deviceId: String? = null,
)

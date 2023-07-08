package com.leijendary.spring.template.iam.api.v1.model

import jakarta.validation.constraints.NotBlank

data class AccountDeleteRequest(
    @field:NotBlank(message = "validation.required")
    val reason: String? = null,
)

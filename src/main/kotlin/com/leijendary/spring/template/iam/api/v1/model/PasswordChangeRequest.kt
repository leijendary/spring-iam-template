package com.leijendary.spring.template.iam.api.v1.model

import com.leijendary.spring.template.iam.entity.UserCredential.Type
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class PasswordChangeRequest(
    @field:NotNull(message = "validation.required")
    val field: Type? = null,

    @field:NotBlank(message = "validation.required")
    val currentPassword: String? = null,

    @field:NotBlank(message = "validation.required")
    val password: String? = null
)

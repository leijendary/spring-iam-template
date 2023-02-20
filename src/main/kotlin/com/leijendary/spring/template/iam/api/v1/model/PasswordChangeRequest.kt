package com.leijendary.spring.template.iam.api.v1.model

import jakarta.validation.constraints.NotBlank

data class PasswordChangeRequest(
    @field:NotBlank(message = "validation.required")
    val currentPassword: String? = null,

    @field:NotBlank(message = "validation.required")
    val password: String? = null
) : PasswordVerifyRequest()

package com.leijendary.spring.template.iam.api.v1.model

import jakarta.validation.constraints.NotBlank

data class PasswordResetRequest(
    @field:NotBlank(message = "validation.required")
    val password: String? = null,

    @field:NotBlank(message = "validation.required")
    val verificationCode: String? = null
) : CredentialFieldRequest()

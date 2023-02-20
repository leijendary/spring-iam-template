package com.leijendary.spring.template.iam.api.v1.model

import jakarta.validation.constraints.NotBlank

data class PasswordNominateRequest(
    @field:NotBlank(message = "validation.required")
    val password: String? = null
) : VerifyRequest()

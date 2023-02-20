package com.leijendary.spring.template.iam.api.v1.model

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class EmailUpdateRequest(
    @field:NotBlank(message = "validation.required")
    @field:Email(message = "validation.email.invalid")
    val email: String? = null,

    @field:NotBlank(message = "validation.required")
    val deviceId: String? = null,
)

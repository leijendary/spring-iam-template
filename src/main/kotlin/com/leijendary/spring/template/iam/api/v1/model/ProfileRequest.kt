package com.leijendary.spring.template.iam.api.v1.model

import jakarta.validation.constraints.NotBlank

data class ProfileRequest(
    @field:NotBlank(message = "validation.required")
    val firstName: String? = null,

    val middleName: String? = null,

    @field:NotBlank(message = "validation.required")
    val lastName: String? = null,
)

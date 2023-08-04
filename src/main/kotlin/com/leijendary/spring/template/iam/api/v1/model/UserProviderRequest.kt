package com.leijendary.spring.template.iam.api.v1.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UserProviderRequest(
    @field:NotBlank(message = "validation.required")
    @field:Size(max = 20, message = "validation.maxLength")
    val provider: String? = null,

    @field:NotBlank(message = "validation.required")
    @field:Size(max = 10, message = "validation.maxLength")
    val type: String? = null,

    @field:NotBlank(message = "validation.required")
    @field:Size(max = 36, message = "validation.maxLength")
    val reference: String? = null,
)

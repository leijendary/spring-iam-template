package com.leijendary.spring.template.iam.api.v1.model

import jakarta.validation.constraints.NotBlank

data class PermissionRequest(
    @field:NotBlank(message = "validation.required")
    val value: String? = null,
)

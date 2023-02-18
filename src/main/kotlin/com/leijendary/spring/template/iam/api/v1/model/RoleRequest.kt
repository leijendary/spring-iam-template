package com.leijendary.spring.template.iam.api.v1.model

import jakarta.validation.constraints.NotBlank

data class RoleRequest(
    @field:NotBlank(message = "validation.required")
    val name: String? = null,

    val description: String? = null
)

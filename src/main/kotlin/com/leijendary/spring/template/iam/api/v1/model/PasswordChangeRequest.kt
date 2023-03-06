package com.leijendary.spring.template.iam.api.v1.model

import com.leijendary.spring.template.iam.core.validator.annotation.EnumField
import com.leijendary.spring.template.iam.entity.UserCredential
import jakarta.validation.constraints.NotBlank

data class PasswordChangeRequest(
    @field:NotBlank(message = "validation.required")
    @field:EnumField(enumClass = UserCredential.Type::class)
    val field: String? = null,

    @field:NotBlank(message = "validation.required")
    val currentPassword: String? = null,

    @field:NotBlank(message = "validation.required")
    val password: String? = null
)

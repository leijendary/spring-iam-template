package com.leijendary.spring.template.iam.api.v1.model

import com.leijendary.spring.template.iam.core.validator.annotation.EnumField
import com.leijendary.spring.template.iam.entity.UserCredential
import jakarta.validation.constraints.NotBlank

data class PasswordChangeInitiateRequest(
    @field:NotBlank(message = "validation.required")
    @EnumField(enumClass = UserCredential.Type::class)
    val preferredUsername: String? = null,

    @field:NotBlank(message = "validation.required")
    val deviceId: String? = null,
)

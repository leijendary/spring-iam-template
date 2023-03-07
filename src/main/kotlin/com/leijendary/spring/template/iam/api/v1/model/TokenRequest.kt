package com.leijendary.spring.template.iam.api.v1.model

import com.leijendary.spring.template.iam.core.validator.annotation.EnumField
import com.leijendary.spring.template.iam.model.DevicePlatform
import jakarta.validation.constraints.NotBlank

data class TokenRequest(
    @field:NotBlank(message = "validation.required")
    val username: String? = null,

    @field:NotBlank(message = "validation.required")
    val password: String? = null,

    @field:NotBlank(message = "validation.required")
    val audience: String? = null,

    @field:NotBlank(message = "validation.required")
    val deviceId: String? = null,

    @field:NotBlank(message = "validation.required")
    @field:EnumField(enumClass = DevicePlatform::class)
    val platform: String? = null,
)

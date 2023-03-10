package com.leijendary.spring.template.iam.api.v1.model

import com.leijendary.spring.template.iam.core.validator.annotation.EnumField
import com.leijendary.spring.template.iam.model.DevicePlatform
import com.leijendary.spring.template.iam.model.SocialProvider
import jakarta.validation.constraints.NotBlank

data class SocialRequest(
    @field:NotBlank(message = "validation.required")
    val token: String? = null,

    @field:NotBlank(message = "validation.required")
    @field:EnumField(enumClass = SocialProvider::class)
    val provider: String? = null,

    @field:NotBlank(message = "validation.required")
    val deviceId: String? = null,

    @field:NotBlank(message = "validation.required")
    @field:EnumField(enumClass = DevicePlatform::class)
    val platform: String? = null,
)

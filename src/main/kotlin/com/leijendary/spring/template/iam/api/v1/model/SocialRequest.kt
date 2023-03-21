package com.leijendary.spring.template.iam.api.v1.model

import com.leijendary.spring.template.iam.entity.UserSocial.Provider
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class SocialRequest(
    @field:NotBlank(message = "validation.required")
    val token: String? = null,

    @field:NotNull(message = "validation.required")
    val provider: Provider? = null
)

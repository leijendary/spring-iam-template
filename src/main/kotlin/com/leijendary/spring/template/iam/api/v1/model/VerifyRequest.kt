package com.leijendary.spring.template.iam.api.v1.model

import jakarta.validation.constraints.NotBlank

// TODO: remove
open class VerifyRequest(

    @field:NotBlank(message = "validation.required")
    val deviceId: String? = null,
)

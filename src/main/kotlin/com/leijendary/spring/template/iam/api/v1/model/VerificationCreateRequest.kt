package com.leijendary.spring.template.iam.api.v1.model

import com.leijendary.spring.template.iam.core.validator.annotation.EnumField
import com.leijendary.spring.template.iam.entity.Verification
import jakarta.validation.constraints.NotBlank

data class VerificationCreateRequest(
    @field:NotBlank(message = "validation.required")
    val deviceId: String? = null,

    @field:NotBlank(message = "validation.required")
    @field:EnumField(enumClass = Verification.Type::class)
    val type: String? = null,
) : CredentialFieldRequest()

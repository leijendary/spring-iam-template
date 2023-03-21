package com.leijendary.spring.template.iam.api.v1.model

import com.leijendary.spring.template.iam.entity.Verification.Type
import jakarta.validation.constraints.NotNull

data class VerificationCreateRequest(
    @field:NotNull(message = "validation.required")
    val type: Type? = null,
) : CredentialFieldRequest()

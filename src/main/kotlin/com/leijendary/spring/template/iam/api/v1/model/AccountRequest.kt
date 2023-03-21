package com.leijendary.spring.template.iam.api.v1.model

import com.leijendary.spring.template.iam.entity.Account.Type
import jakarta.validation.constraints.NotNull

data class AccountRequest(
    @field:NotNull(message = "validation.required")
    val type: Type? = null
)

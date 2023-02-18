package com.leijendary.spring.template.iam.api.v1.model

import com.leijendary.spring.template.iam.core.validator.annotation.EnumField
import com.leijendary.spring.template.iam.entity.Account
import jakarta.validation.constraints.NotBlank

data class AccountRequest(
    @field:NotBlank(message = "validation.required")
    @field:EnumField(enumClass = Account.Type::class)
    val type: String? = null
)

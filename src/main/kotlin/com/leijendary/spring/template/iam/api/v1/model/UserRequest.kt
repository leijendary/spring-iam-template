package com.leijendary.spring.template.iam.api.v1.model

import com.leijendary.spring.template.iam.core.validator.annotation.EnumField
import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.validator.annotation.Phone
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class UserRequest(
    @field:Valid
    val account: AccountRequest? = null,

    @field:NotNull(message = "validation.required")
    @field:Valid
    val role: UuidRequest? = null,

    @field:NotBlank(message = "validation.required")
    val firstName: String? = null,

    val middleName: String? = null,

    @field:NotBlank(message = "validation.required")
    val lastName: String? = null,

    @field:NotBlank(message = "validation.required")
    @field:Email(message = "validation.email.invalid")
    val email: String? = null,

    @field:NotBlank(message = "validation.required")
    val countryCode: String? = null,

    @field:NotBlank(message = "validation.required")
    @field:Phone
    val phone: String? = null,

    @field:NotBlank(message = "validation.required")
    @field:EnumField(enumClass = UserCredential.Type::class)
    val preferredUsername: String? = null,
)
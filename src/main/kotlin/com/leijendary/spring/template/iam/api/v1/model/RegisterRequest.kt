package com.leijendary.spring.template.iam.api.v1.model

import com.leijendary.spring.template.iam.entity.UserCredential
import jakarta.validation.constraints.NotBlank

sealed class RegisterRequest {
    @field:NotBlank(message = "validation.required")
    val firstName: String? = null

    val middleName: String? = null

    @field:NotBlank(message = "validation.required")
    val lastName: String? = null

    @field:NotBlank(message = "validation.required")
    val password: String? = null

    @field:NotBlank(message = "validation.required")
    val verificationCode: String? = null

    abstract val username: String
    abstract val credentialType: UserCredential.Type
}

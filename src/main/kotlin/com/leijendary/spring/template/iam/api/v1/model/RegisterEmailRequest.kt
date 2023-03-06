package com.leijendary.spring.template.iam.api.v1.model

import com.leijendary.spring.template.iam.entity.UserCredential
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class RegisterEmailRequest(
    @field:NotBlank(message = "validation.required")
    @field:Email(message = "validation.email.invalid")
    val email: String? = null,
) : RegisterRequest() {
    override val username: String
        get() = email!!

    override val credentialType: UserCredential.Type
        get() = UserCredential.Type.EMAIL
}

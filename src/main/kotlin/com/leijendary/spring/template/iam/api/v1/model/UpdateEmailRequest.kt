package com.leijendary.spring.template.iam.api.v1.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.entity.Verification
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class UpdateEmailRequest(
    @field:NotBlank(message = "validation.required")
    @field:Email(message = "validation.email.invalid")
    val email: String? = null,
) : UpdateUsernameRequest() {
    @get:JsonIgnore
    override val username: String
        get() = email!!

    @get:JsonIgnore
    override val credentialType: UserCredential.Type
        get() = UserCredential.Type.EMAIL

    @get:JsonIgnore
    override val verificationType: Verification.Type
        get() = Verification.Type.EMAIL_CHANGE
}

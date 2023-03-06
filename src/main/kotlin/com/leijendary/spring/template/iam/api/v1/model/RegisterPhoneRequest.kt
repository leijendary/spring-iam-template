package com.leijendary.spring.template.iam.api.v1.model

import com.leijendary.spring.template.iam.core.projection.PhoneProjection
import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.validator.annotation.Phone
import jakarta.validation.constraints.NotBlank

data class RegisterPhoneRequest(
    @field:NotBlank(message = "validation.required")
    override val countryCode: String? = null,

    @field:NotBlank(message = "validation.required")
    @field:Phone
    override val phone: String? = null,
) : RegisterRequest(), PhoneProjection {
    override val username: String
        get() = fullPhone

    override val credentialType: UserCredential.Type
        get() = UserCredential.Type.PHONE
}

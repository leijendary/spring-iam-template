package com.leijendary.spring.template.iam.api.v1.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.leijendary.spring.template.iam.core.projection.PhoneProjection
import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.validator.annotation.Phone
import jakarta.validation.constraints.NotBlank

data class RegisterPhoneRequest(
    @field:NotBlank(message = "validation.required")
    override var countryCode: String? = null,

    @field:NotBlank(message = "validation.required")
    @field:Phone
    override var phone: String? = null,
) : RegisterRequest(), PhoneProjection {
    @get:JsonIgnore
    override val username: String
        get() = fullPhone

    @get:JsonIgnore
    override val credentialType: UserCredential.Type
        get() = UserCredential.Type.PHONE
}

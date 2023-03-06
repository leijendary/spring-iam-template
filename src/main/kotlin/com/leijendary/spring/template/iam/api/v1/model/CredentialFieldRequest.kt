package com.leijendary.spring.template.iam.api.v1.model

import com.leijendary.spring.template.iam.core.validator.annotation.EnumField
import com.leijendary.spring.template.iam.entity.UserCredential
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

open class CredentialFieldRequest {
    @field:NotBlank(message = "validation.required")
    @field:EnumField(enumClass = UserCredential.Type::class)
    val field: String? = null

    @Schema(
        description = """
            Either the email address (if field is email) or the country code + phone number (if field is phone).
            For the phone number it should look like: "+1 123456" (with space).
        """
    )
    @field:NotBlank(message = "validation.required")
    val value: String? = null
}

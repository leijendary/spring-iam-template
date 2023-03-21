package com.leijendary.spring.template.iam.api.v1.model

import com.leijendary.spring.template.iam.entity.UserCredential.Type
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

open class CredentialFieldRequest {
    @field:NotNull(message = "validation.required")
    val field: Type? = null

    @Schema(
        description = """
            Either the email address (if field is email) or the country code + phone number (if field is phone).
            For the phone number it should look like: "+1 123456" (with space).
        """
    )
    @field:NotBlank(message = "validation.required")
    val value: String? = null
}

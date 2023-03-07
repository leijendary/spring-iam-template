package com.leijendary.spring.template.iam.api.v1.model

import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.entity.Verification
import jakarta.validation.constraints.NotBlank

sealed class UpdateUsernameRequest {
    @field:NotBlank(message = "validation.required")
    val verificationCode: String? = null

    @field:NotBlank(message = "validation.required")
    val deviceId: String? = null

    abstract val username: String
    abstract val credentialType: UserCredential.Type
    abstract val verificationType: Verification.Type
}

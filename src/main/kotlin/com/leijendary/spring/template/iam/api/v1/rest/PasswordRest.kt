package com.leijendary.spring.template.iam.api.v1.rest

import com.leijendary.spring.template.iam.api.v1.model.PasswordChangeRequest
import com.leijendary.spring.template.iam.api.v1.model.PasswordNominateRequest
import com.leijendary.spring.template.iam.api.v1.model.PasswordResetRequest
import com.leijendary.spring.template.iam.api.v1.service.PasswordService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/password")
@Tag(
    name = "Password",
    description = "Password API reference. All actions regarding password should be here including nominate password."
)
class PasswordRest(private val passwordService: PasswordService) {
    @PostMapping
    @Operation(summary = "Nominate the password based on the verification code.")
    fun nominate(@Valid @RequestBody request: PasswordNominateRequest) = passwordService.nominate(request)

    @PutMapping
    @Operation(summary = "Change the current user's password based on the field.")
    fun change(@Valid @RequestBody request: PasswordChangeRequest) = passwordService.change(request)

    @PostMapping("reset")
    @Operation(summary = "Set the new password based on the credentials.")
    fun reset(@Valid @RequestBody request: PasswordResetRequest) = passwordService.reset(request)
}

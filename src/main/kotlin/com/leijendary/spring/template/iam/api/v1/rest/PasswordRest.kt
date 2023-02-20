package com.leijendary.spring.template.iam.api.v1.rest

import com.leijendary.spring.template.iam.api.v1.model.*
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
    @PostMapping("reset")
    @Operation(summary = "Create a verification code for the reset password flow.")
    fun reset(@Valid @RequestBody request: PasswordResetRequest) = passwordService.reset(request)

    @PostMapping("reset/verify")
    @Operation(
        summary = """
            Use the verification code from the reset password initiation to create a 
            verification code for the nominate password process.
        """
    )
    fun resetVerify(@Valid @RequestBody request: PasswordVerifyRequest) = passwordService.resetVerify(request)

    @PostMapping("nominate")
    @Operation(summary = "Create a password using the verification code sent.")
    fun nominate(@Valid @RequestBody request: PasswordNominateRequest) = passwordService.nominate(request)

    @PostMapping("change/initiate")
    @Operation(summary = "Initiate the process of changing the user's password.")
    fun changeInitiate(@Valid @RequestBody request: PasswordChangeInitiateRequest): NextCode {
        return passwordService.changeInitiate(request)
    }

    @PostMapping("change/verify")
    @Operation(summary = "Verify the process of changing the user's password.")
    fun changeVerify(@Valid @RequestBody request: PasswordVerifyRequest) = passwordService.changeVerify(request)

    @PutMapping("change")
    @Operation(summary = "The actual update of the user's password.")
    fun change(@Valid @RequestBody request: PasswordChangeRequest) = passwordService.change(request)
}

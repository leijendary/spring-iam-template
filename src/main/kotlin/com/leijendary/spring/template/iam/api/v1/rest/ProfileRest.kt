package com.leijendary.spring.template.iam.api.v1.rest

import com.leijendary.spring.template.iam.api.v1.model.EmailUpdateRequest
import com.leijendary.spring.template.iam.api.v1.model.PhoneUpdateRequest
import com.leijendary.spring.template.iam.api.v1.model.ProfileRequest
import com.leijendary.spring.template.iam.api.v1.model.VerifyRequest
import com.leijendary.spring.template.iam.api.v1.service.ProfileService
import com.leijendary.spring.template.iam.core.util.RequestContext.userIdOrSystem
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/profile")
@Tag(
    name = "Profile",
    description = "Profile API reference. Anything regarding the profile activity are included here."
)
class ProfileRest(private val profileService: ProfileService) {
    @GetMapping
    @Operation(summary = "Gets the current user's profile details")
    fun detail() = profileService.detail(userIdOrSystem)

    @PutMapping
    @Operation(
        summary = """
            Update the current user's profile details except those fields that are related to the username 
            that needs verification like email and phone number.
        """
    )
    fun update(@Valid @RequestBody request: ProfileRequest) = profileService.update(userIdOrSystem, request)

    @PatchMapping("email")
    @Operation(summary = "Change the user's email. This will send a verification email.")
    fun email(@Valid @RequestBody request: EmailUpdateRequest) = profileService.email(request)

    @PostMapping("email/verify")
    @Operation(summary = "Set the email as verified once the verification code is proved valid.")
    fun emailVerify(@Valid @RequestBody request: VerifyRequest) = profileService.emailVerify(request)

    @PatchMapping("phone")
    @Operation(summary = "Change the user's phone number. This will send a verification SMS.")
    fun phone(@Valid @RequestBody request: PhoneUpdateRequest) = profileService.phone(request)

    @PostMapping("phone/verify")
    @Operation(summary = "Set the phone as verified once the verification code is proved valid.")
    fun phoneVerify(@Valid @RequestBody request: VerifyRequest) = profileService.phoneVerify(request)
}

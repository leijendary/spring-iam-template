package com.leijendary.spring.template.iam.api.v1.rest

import com.leijendary.spring.template.iam.api.v1.model.ProfileRequest
import com.leijendary.spring.template.iam.api.v1.model.UpdateEmailRequest
import com.leijendary.spring.template.iam.api.v1.model.UpdatePhoneRequest
import com.leijendary.spring.template.iam.api.v1.service.ProfileService
import com.leijendary.spring.template.iam.core.util.RequestContext.userIdOrThrow
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/profile")
@Tag(
    name = "Profile",
    description = "Profile API reference. Anything regarding the profile activity are included here."
)
@SecurityRequirement(name = AUTHORIZATION)
class ProfileRest(private val profileService: ProfileService) {
    @GetMapping
    @Operation(summary = "Gets the current user's profile details")
    fun detail() = profileService.detail(userIdOrThrow)

    @PutMapping
    @Operation(
        summary = """
            Update the current user's profile details except those fields that are related to the username 
            that needs verification like email and phone number.
        """
    )
    fun update(@Valid @RequestBody request: ProfileRequest) = profileService.update(userIdOrThrow, request)

    @PatchMapping("email")
    @Operation(summary = "Change the user's email. This requires a verification.")
    fun email(@Valid @RequestBody request: UpdateEmailRequest) = profileService.username(request)

    @PatchMapping("phone")
    @Operation(summary = "Change the user's phone number. This requires a verification.")
    fun phone(@Valid @RequestBody request: UpdatePhoneRequest) = profileService.username(request)
}

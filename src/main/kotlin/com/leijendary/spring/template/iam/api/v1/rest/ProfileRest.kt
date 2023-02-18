package com.leijendary.spring.template.iam.api.v1.rest

import com.leijendary.spring.template.iam.api.v1.model.ProfileRequest
import com.leijendary.spring.template.iam.api.v1.service.ProfileService
import com.leijendary.spring.template.iam.core.util.RequestContext.userId
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
    fun detail() = profileService.detail(userId)

    @PutMapping
    @Operation(
        summary = """
            Update the current user's profile details except those fields that are related to the username 
            that needs verification like email and mobile number
        """
    )
    fun update(@Valid @RequestBody request: ProfileRequest) = profileService.update(userId, request)
}

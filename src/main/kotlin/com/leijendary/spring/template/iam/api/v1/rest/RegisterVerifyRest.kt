package com.leijendary.spring.template.iam.api.v1.rest

import com.leijendary.spring.template.iam.api.v1.model.VerifyRequest
import com.leijendary.spring.template.iam.api.v1.service.RegisterVerifyService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/register/verify")
@Tag(
    name = "Register Verify",
    description = """
        API for the verifying the registration flow. After this, the user should go 
        to the nominate password flow (if no password yet).
    """
)
class RegisterVerifyRest(private val registerVerifyService: RegisterVerifyService) {
    @PostMapping
    @Operation(
        summary = """
            Verify the user's registration. If the user doesn't have a password yet, 
            a nominate pin verification code will return
        """
    )
    fun verify(@Valid @RequestBody request: VerifyRequest) = registerVerifyService.verify(request)
}

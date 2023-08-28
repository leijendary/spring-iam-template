package com.leijendary.spring.template.iam.api.v1.rest

import com.leijendary.spring.template.iam.api.v1.model.RegisterEmailRequest
import com.leijendary.spring.template.iam.api.v1.model.RegisterPhoneRequest
import com.leijendary.spring.template.iam.api.v1.service.RegisterService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/register")
@Tag(name = "Register", description = ".")
class RegisterRest(private val registerService: RegisterService) {
    @PostMapping("email")
    @Operation(
        summary = """
            A customer account can register using this API. This API requires the full information of
            the user, then the email will be the verification method. The verificationCode will
            assume that the verification API is called before this API is called.
        """
    )
    fun email(@Valid @RequestBody request: RegisterEmailRequest) = registerService.register(request)

    @PostMapping("phone")
    @Operation(
        summary = """
            A customer account can register using this API. This API requires the full information of
            the user, then the phone number will be the verification method. The verificationCode will
            assume that the verification API is called before this API is called.
        """
    )
    fun phone(@Valid @RequestBody request: RegisterPhoneRequest) = registerService.register(request)
}

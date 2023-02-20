package com.leijendary.spring.template.iam.api.v1.rest

import com.leijendary.spring.template.iam.api.v1.model.NextCode
import com.leijendary.spring.template.iam.api.v1.model.RegisterCustomerEmailRequest
import com.leijendary.spring.template.iam.api.v1.model.RegisterCustomerFullRequest
import com.leijendary.spring.template.iam.api.v1.model.RegisterCustomerPhoneRequest
import com.leijendary.spring.template.iam.api.v1.service.RegisterCustomerService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/register/customer")
@Tag(name = "Register Customer", description = "API for the registration flows of the customer.")
class RegisterCustomerRest(private val registerCustomerService: RegisterCustomerService) {
    @PostMapping("email")
    @ResponseStatus(ACCEPTED)
    @Operation(
        summary = """
            A customer account can register using this API. Although, this API only requires the
            email and deviceId. This is only for the email credential. Once registered, this API 
            will send a verification to the email address.
        """
    )
    fun email(@Valid @RequestBody request: RegisterCustomerEmailRequest): NextCode {
        return registerCustomerService.email(request)
    }

    @PostMapping("phone")
    @ResponseStatus(ACCEPTED)
    @Operation(
        summary = """
            A customer account can register using this API. Although, this API only requires the
            countryCode, phone, and deviceId. This is only for the phone number credential.
            Once registered, this API will send an SMS verification to the phone number for 
            verification.
        """
    )
    fun phone(@Valid @RequestBody request: RegisterCustomerPhoneRequest): NextCode {
        return registerCustomerService.phone(request)
    }

    @PostMapping("full")
    @ResponseStatus(ACCEPTED)
    @Operation(
        summary = """
            A customer account can register using this API. This API requires the full information of
            the user. preferredUsername is required to identify what the username should be. Once registered,
            this API should send a verification to the preferredUsername
        """
    )
    fun full(@Valid @RequestBody request: RegisterCustomerFullRequest): NextCode {
        return registerCustomerService.full(request)
    }
}

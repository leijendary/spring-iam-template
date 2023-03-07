package com.leijendary.spring.template.iam.api.v1.rest

import com.leijendary.spring.template.iam.api.v1.model.VerificationCreateRequest
import com.leijendary.spring.template.iam.api.v1.service.VerificationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/verification")
@Tag(name = "Verification", description = "APIs that relates to verifications like create a verification code.")
class VerificationRest(private val verificationService: VerificationService) {
    @PostMapping
    @Operation(
        summary = """
            Create a verification code based on the type and field provided. 
            This will also send the verification code to the specific field provided
        """
    )
    fun create(@Valid @RequestBody request: VerificationCreateRequest) = verificationService.create(request)
}

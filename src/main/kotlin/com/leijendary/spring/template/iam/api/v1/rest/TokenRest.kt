package com.leijendary.spring.template.iam.api.v1.rest

import com.leijendary.spring.template.iam.api.v1.model.SocialRequest
import com.leijendary.spring.template.iam.api.v1.model.TokenRefreshRequest
import com.leijendary.spring.template.iam.api.v1.model.TokenRequest
import com.leijendary.spring.template.iam.api.v1.model.TokenRevokeRequest
import com.leijendary.spring.template.iam.api.v1.service.TokenService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/token")
@Tag(
    name = "Token",
    description = "Endpoints for token related actions like getting a token, refreshing a token, or revoking a token."
)
class TokenRest(private val tokenService: TokenService) {
    @PostMapping
    @ResponseStatus(CREATED)
    @Operation(
        summary = """
            Creates an access token with refresh token. The tokens will be saved to the database for reference
            until revoked.
        """
    )
    fun create(@Valid @RequestBody request: TokenRequest) = tokenService.create(request)

    @PostMapping("refresh")
    @Operation(
        summary = """
            Refreshes an access token using the refresh token provided. The refresh token must exist and
            the expiration date should be after the current date
        """
    )
    fun refresh(@Valid @RequestBody request: TokenRefreshRequest) = tokenService.refresh(request)

    @DeleteMapping
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "Revokes the access token.")
    fun revoke(@Valid @RequestBody request: TokenRevokeRequest) = tokenService.revoke(request)

    @PostMapping("social")
    @Operation(summary = "Verify that the token provided here is a valid social login ID")
    fun social(@Valid @RequestBody request: SocialRequest) = tokenService.social(request)
}

package com.leijendary.spring.template.iam.api.v1.rest

import com.leijendary.spring.template.iam.api.v1.model.AccountDeleteRequest
import com.leijendary.spring.template.iam.api.v1.service.AccountService
import com.leijendary.spring.template.iam.core.util.RequestContext.userIdOrThrow
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(
    name = "Account",
    description = "Account API reference. These APIs might affect the status of the users attached to the account."
)
@SecurityRequirement(name = AUTHORIZATION)
class AccountRest(private val accountService: AccountService) {
    @DeleteMapping
    @Operation(summary = "Delete the current user's account. This will also delete all users associated in the account.")
    @ResponseStatus(NO_CONTENT)
    fun delete(@Valid @RequestBody accountDeleteRequest: AccountDeleteRequest) {
        accountService.delete(userIdOrThrow, accountDeleteRequest)
    }
}

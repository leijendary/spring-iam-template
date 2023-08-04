package com.leijendary.spring.template.iam.api.v1.rest

import com.leijendary.spring.template.iam.api.v1.model.UserProviderRequest
import com.leijendary.spring.template.iam.api.v1.model.UserProviderResponse
import com.leijendary.spring.template.iam.api.v1.service.UserProviderService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/users/{userId}/providers")
@Tag(name = "User Provider", description = "Connection between the user, provider, and the type of reference.")
class UserProviderRest(private val userProviderService: UserProviderService) {
    @GetMapping
    @Operation(summary = "Get the paginated list of the user's providers.")
    fun list(@PathVariable userId: UUID, pageable: Pageable) = userProviderService.list(userId, pageable)

    @PostMapping
    @Operation(summary = "Creates a user provider type reference.")
    fun create(@PathVariable userId: UUID, @Valid @RequestBody request: UserProviderRequest): UserProviderResponse {
        return userProviderService.create(userId, request)
    }

    @GetMapping("{provider}/{type}")
    @Operation(summary = "Retrieves the details based on the provider and the specified type of reference.")
    fun get(
        @PathVariable userId: UUID,
        @PathVariable provider: String,
        @PathVariable type: String
    ): UserProviderResponse {
        return userProviderService.get(userId, provider, type)
    }
}

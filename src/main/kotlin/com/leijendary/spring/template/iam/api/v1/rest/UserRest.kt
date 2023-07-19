package com.leijendary.spring.template.iam.api.v1.rest

import com.leijendary.spring.template.iam.api.v1.model.UserExclusionQueryRequest
import com.leijendary.spring.template.iam.api.v1.model.UserRequest
import com.leijendary.spring.template.iam.api.v1.service.UserService
import com.leijendary.spring.template.iam.core.model.QueryRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User", description = "User resource API. All users that are not connected to an account are shown here.")
@SecurityRequirement(name = AUTHORIZATION)
class UserRest(private val userService: UserService) {
    @GetMapping
    @Operation(summary = "Get the paginated list of users without an account.")
    fun list(
        queryRequest: QueryRequest,
        userExclusionQueryRequest: UserExclusionQueryRequest,
        pageable: Pageable
    ) = userService.list(queryRequest, userExclusionQueryRequest, pageable)

    @PostMapping
    @ResponseStatus(CREATED)
    @Operation(summary = "Saves a user into the database. Both email and phone should be unique.")
    fun create(@Valid @RequestBody request: UserRequest) = userService.create(request)

    @GetMapping("{id}")
    @Operation(summary = "Retrieves the user details from the database.")
    fun get(@PathVariable id: UUID) = userService.get(id)

    @PutMapping("{id}")
    @Operation(summary = "Updates the user record into the database. The user's email and phone should be unique.")
    fun update(@PathVariable id: UUID, @Valid @RequestBody request: UserRequest) = userService.update(id, request)

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    fun delete(@PathVariable id: UUID) = userService.delete(id)
}

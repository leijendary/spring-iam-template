package com.leijendary.spring.template.iam.api.v1.rest

import com.leijendary.spring.template.iam.api.v1.model.RoleRequest
import com.leijendary.spring.template.iam.api.v1.model.RoleResponse
import com.leijendary.spring.template.iam.api.v1.service.RoleService
import com.leijendary.spring.template.iam.core.model.QueryRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/roles")
@Tag(name = "Role", description = "Role resource API. Roles are a group of permissions that can be assigned to a user.")
class RoleRest(private val roleService: RoleService) {
    @GetMapping
    @Operation(summary = "Get the paginated list of roles")
    fun list(queryRequest: QueryRequest, pageable: Pageable) = roleService.list(queryRequest, pageable)

    @PostMapping
    @ResponseStatus(CREATED)
    @Operation(summary = "Saves a role into the database. The name must be unique.")
    fun create(@Valid @RequestBody request: RoleRequest) = roleService.create(request)

    @GetMapping("{id}")
    @Operation(summary = "Retrieves the role from the database.")
    fun get(@PathVariable id: UUID) = roleService.get(id)

    @PutMapping("{id}")
    @Operation(summary = "Updates the role into the database. The name should be unique.")
    fun update(@PathVariable id: UUID, @Valid @RequestBody request: RoleRequest): RoleResponse {
        return roleService.update(id, request)
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "Removes the role from the database.")
    fun delete(@PathVariable id: UUID) = roleService.delete(id)
}

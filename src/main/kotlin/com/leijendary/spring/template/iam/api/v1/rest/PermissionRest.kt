package com.leijendary.spring.template.iam.api.v1.rest

import com.leijendary.spring.template.iam.api.v1.model.PermissionRequest
import com.leijendary.spring.template.iam.api.v1.model.PermissionResponse
import com.leijendary.spring.template.iam.api.v1.service.PermissionService
import com.leijendary.spring.template.iam.core.model.QueryRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/permissions")
@Tag(
    name = "Permission",
    description = "Permission resource API. All permissions should be in an urn format (prefixed with 'urn:')."
)
class PermissionRest(private val permissionService: PermissionService) {
    @GetMapping
    @Operation(summary = "Get the paginated list of permissions.")
    fun list(queryRequest: QueryRequest, pageable: Pageable) = permissionService.list(queryRequest, pageable)

    @PostMapping
    @ResponseStatus(CREATED)
    @Operation(summary = "Saves a permission into the database. The permission should be unique.")
    fun create(@Valid @RequestBody request: PermissionRequest) = permissionService.create(request)

    @GetMapping("{id}")
    @Operation(summary = "Retrieves the permission from the database.")
    fun get(@PathVariable id: Long) = permissionService.get(id)

    @PutMapping("{id}")
    @Operation(summary = "Updates the permission record into the database. The permission should be unique.")
    fun update(@PathVariable id: Long, @Valid @RequestBody request: PermissionRequest): PermissionResponse {
        return permissionService.update(id, request)
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "Removes the permission record from the database.")
    fun delete(@PathVariable id: Long) = permissionService.delete(id)
}

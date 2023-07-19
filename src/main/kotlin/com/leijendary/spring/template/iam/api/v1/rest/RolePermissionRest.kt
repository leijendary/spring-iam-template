package com.leijendary.spring.template.iam.api.v1.rest

import com.leijendary.spring.template.iam.api.v1.model.PermissionResponse
import com.leijendary.spring.template.iam.api.v1.model.RolePermissionRequest
import com.leijendary.spring.template.iam.api.v1.service.RolePermissionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/roles/{roleId}/permissions")
@Tag(
    name = "Role Permission",
    description = "Role Permission resource API. Contains all the permission APIs under a specific role"
)
class RolePermissionRest(private val rolePermissionService: RolePermissionService) {
    @GetMapping
    @Operation(description = "Get the list of permissions")
    fun list(@PathVariable roleId: UUID) = rolePermissionService.list(roleId)

    @PostMapping
    fun add(@PathVariable roleId: UUID, @Valid @RequestBody request: RolePermissionRequest): List<PermissionResponse> {
        return rolePermissionService.add(roleId, request)
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    fun remove(@PathVariable roleId: UUID, @PathVariable id: Long) = rolePermissionService.remove(roleId, id)
}

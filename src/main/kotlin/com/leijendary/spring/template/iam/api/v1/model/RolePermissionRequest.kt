package com.leijendary.spring.template.iam.api.v1.model

import jakarta.validation.Valid
import jakarta.validation.constraints.Size

data class RolePermissionRequest(
    @field:Size(min = 1, message = "validation.min")
    @field:Valid
    val permissions: Set<IdRequest> = HashSet()
)

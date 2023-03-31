package com.leijendary.spring.template.iam.api.v1.model

import jakarta.validation.constraints.NotNull
import java.util.*

data class UUIDRequest(
    @field:NotNull(message = "validation.required")
    val id: UUID? = null
)

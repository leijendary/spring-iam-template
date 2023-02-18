package com.leijendary.spring.template.iam.api.v1.model

import jakarta.validation.constraints.Min

data class IdRequest(
    @field:Min(value = 1, message = "validation.min")
    val id: Long = 0
)

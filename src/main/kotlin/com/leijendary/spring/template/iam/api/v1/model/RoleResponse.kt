package com.leijendary.spring.template.iam.api.v1.model

import java.io.Serializable
import java.util.*

data class RoleResponse(val id: UUID, val name: String, val description: String? = null) : Serializable

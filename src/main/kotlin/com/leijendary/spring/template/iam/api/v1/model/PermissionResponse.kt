package com.leijendary.spring.template.iam.api.v1.model

import java.io.Serializable

data class PermissionResponse(val id: Long, val permission: String) : Serializable

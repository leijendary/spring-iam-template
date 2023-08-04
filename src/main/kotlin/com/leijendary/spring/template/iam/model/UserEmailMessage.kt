package com.leijendary.spring.template.iam.model

import java.util.*

data class UserEmailMessage(val id: UUID, val email: String, val emailVerified: Boolean)

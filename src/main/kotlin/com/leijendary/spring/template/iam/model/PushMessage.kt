package com.leijendary.spring.template.iam.model

import java.util.*

data class PushMessage(
    val userId: UUID,
    val title: String,
    val body: String,
    val imageUrl: String?,
    val isRead: Boolean
)

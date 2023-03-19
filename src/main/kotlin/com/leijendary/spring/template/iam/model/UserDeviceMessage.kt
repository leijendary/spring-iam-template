package com.leijendary.spring.template.iam.model

import java.util.*

data class UserDeviceMessage(val userId: UUID, val token: String, val platform: String)
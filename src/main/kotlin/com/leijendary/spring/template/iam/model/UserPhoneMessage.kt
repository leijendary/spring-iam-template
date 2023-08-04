package com.leijendary.spring.template.iam.model

import java.util.*

data class UserPhoneMessage(val id: UUID, val countryCode: String, val phone: String, val phoneVerified: Boolean)

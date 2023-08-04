package com.leijendary.spring.template.iam.model

import java.util.*

data class UserAddressMessage(
    val id: UUID,
    val userId: UUID,
    val name: String,
    val street: String,
    val additional: String?,
    val city: String,
    val region: String,
    val postalCode: String,
    val country: String,
    val countryCode: String,
    val primary: Boolean
)

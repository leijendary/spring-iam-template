package com.leijendary.spring.template.iam.api.v1.model

import java.io.Serializable
import java.util.*

data class UserAddressResponse(
    val id: UUID,
    val label: String,
    val street: String,
    val additional: String?,
    val city: String,
    val region: String,
    val postalCode: String,
    val country: String,
    val countryCode: String,
    val latitude: Double,
    val longitude: Double,
    val primary: Boolean
) : Serializable

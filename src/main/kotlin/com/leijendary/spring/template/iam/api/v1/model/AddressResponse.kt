package com.leijendary.spring.template.iam.api.v1.model

import java.io.Serializable
import java.util.*

data class AddressResponse(
    val id: UUID,
    val line1: String,
    val line2: String,
    val city: String,
    val region: String,
    val postalCode: String,
    val country: String,
    val countryCode: String,
    val latitude: Double,
    val longitude: Double,
) : Serializable

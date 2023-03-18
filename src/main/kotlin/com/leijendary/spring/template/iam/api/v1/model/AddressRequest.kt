package com.leijendary.spring.template.iam.api.v1.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class AddressRequest(
    @field:NotBlank(message = "validation.required")
    @field:Size(max = 150, message = "validation.maxLength")
    val street: String? = null,

    @field:NotBlank(message = "validation.required")
    @field:Size(max = 60, message = "validation.maxLength")
    val city: String? = null,

    @field:NotBlank(message = "validation.required")
    @field:Size(max = 30, message = "validation.maxLength")
    val region: String? = null,

    @field:NotBlank(message = "validation.required")
    @field:Size(max = 10, message = "validation.maxLength")
    val postalCode: String? = null,

    @field:NotBlank(message = "validation.required")
    @field:Size(max = 56, message = "validation.maxLength")
    val country: String? = null,

    @field:NotBlank(message = "validation.required")
    @field:Size(max = 2, message = "validation.maxLength")
    val countryCode: String? = null,

    @field:NotNull(message = "validation.required")
    val latitude: Double? = null,

    @field:NotNull(message = "validation.required")
    val longitude: Double? = null
)
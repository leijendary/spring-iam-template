package com.leijendary.spring.template.iam.api.v1.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class AddressRequest(
    @field:NotBlank(message = "validation.required")
    @field:Size(message = "validation.maxLength", max = 150)
    val street: String? = null,

    @field:NotBlank(message = "validation.required")
    @field:Size(message = "validation.maxLength", max = 60)
    val city: String? = null,

    @field:NotBlank(message = "validation.required")
    @field:Size(message = "validation.maxLength", max = 30)
    val region: String? = null,

    @field:NotBlank(message = "validation.required")
    @field:Size(message = "validation.maxLength", max = 10)
    val postalCode: String? = null,

    @field:NotBlank(message = "validation.required")
    @field:Size(message = "validation.maxLength", max = 56)
    val country: String? = null,

    @field:NotBlank(message = "validation.required")
    @field:Size(message = "validation.maxLength", max = 2)
    val countryCode: String? = null,

    @field:NotNull(message = "validation.required")
    val latitude: Double? = null,

    @field:NotNull(message = "validation.required")
    val longitude: Double? = null
)

package com.leijendary.spring.template.iam.core.projection

interface PhoneProjection {
    val countryCode: String?
    val phone: String?

    val fullPhone: String
        get() = "$countryCode $phone"
}

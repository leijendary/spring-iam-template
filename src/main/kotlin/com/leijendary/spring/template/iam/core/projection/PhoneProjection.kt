package com.leijendary.spring.template.iam.core.projection

interface PhoneProjection {
    var countryCode: String?
    var phone: String?

    val fullPhone: String
        get() = "$countryCode $phone"
}

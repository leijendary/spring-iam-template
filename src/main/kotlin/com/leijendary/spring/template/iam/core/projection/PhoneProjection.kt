package com.leijendary.spring.template.iam.core.projection

import com.fasterxml.jackson.annotation.JsonIgnore

interface PhoneProjection {
    var countryCode: String?
    var phone: String?

    @get:JsonIgnore
    val fullPhone: String
        get() = "$countryCode $phone"
}

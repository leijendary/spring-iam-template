package com.leijendary.spring.template.iam.core.projection

import java.time.OffsetDateTime

interface LastModifiedProjection {
    var lastModifiedAt: OffsetDateTime
    var lastModifiedBy: String
}

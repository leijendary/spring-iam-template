package com.leijendary.spring.template.iam.core.projection

import java.time.OffsetDateTime

interface CreatedProjection {
    var createdAt: OffsetDateTime
    var createdBy: String
}

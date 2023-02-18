package com.leijendary.spring.template.iam.core.entity

import java.time.OffsetDateTime

interface SoftDeleteEntity {
    var deletedAt: OffsetDateTime?
    var deletedBy: String?
}

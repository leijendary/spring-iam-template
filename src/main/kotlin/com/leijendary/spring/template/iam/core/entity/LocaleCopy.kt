package com.leijendary.spring.template.iam.core.entity

import jakarta.persistence.MappedSuperclass

@MappedSuperclass
open class LocaleCopy : AppEntity() {
    var language: String? = null
    var ordinal: Int = 0
}
package com.leijendary.spring.template.iam.generator

import java.util.*

class UuidCodeGenerationStrategy : CodeGenerationStrategy {
    override fun generate() = UUID.randomUUID().toString()
}

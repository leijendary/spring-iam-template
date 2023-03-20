package com.leijendary.spring.template.iam.generator

import java.util.*

class UUIDCodeGenerationStrategy : CodeGenerationStrategy {
    override fun generate() = UUID.randomUUID().toString()
}

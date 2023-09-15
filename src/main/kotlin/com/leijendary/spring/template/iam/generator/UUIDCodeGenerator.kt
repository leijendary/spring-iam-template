package com.leijendary.spring.template.iam.generator

import java.util.*

class UUIDCodeGenerator : CodeGenerator {
    override fun generate() = UUID.randomUUID().toString()
}

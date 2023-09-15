package com.leijendary.spring.template.iam.generator

import org.apache.commons.lang3.RandomStringUtils

class StringCodeGenerator : CodeGenerator {
    override fun generate(): String = RandomStringUtils.randomAlphabetic(6)
}

package com.leijendary.spring.template.iam.generator

import org.apache.commons.lang3.RandomStringUtils

class CharCodeGenerationStrategy : CodeGenerationStrategy {
    override fun generate(): String = RandomStringUtils.randomAlphabetic(6)
}

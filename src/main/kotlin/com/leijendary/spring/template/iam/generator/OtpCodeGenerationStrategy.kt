package com.leijendary.spring.template.iam.generator

import org.apache.commons.lang3.RandomStringUtils

class OtpCodeGenerationStrategy : CodeGenerationStrategy {
    override fun generate(): String = RandomStringUtils.randomNumeric(6)
}

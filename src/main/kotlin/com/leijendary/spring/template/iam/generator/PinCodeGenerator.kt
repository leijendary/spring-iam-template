package com.leijendary.spring.template.iam.generator

import org.apache.commons.lang3.RandomStringUtils

class PinCodeGenerator : CodeGenerator {
    override fun generate(): String = RandomStringUtils.randomNumeric(6)
}

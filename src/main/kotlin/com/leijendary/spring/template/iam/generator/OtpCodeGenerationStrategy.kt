package com.leijendary.spring.template.iam.generator

import java.util.*

class OtpCodeGenerationStrategy : CodeGenerationStrategy {
    override fun generate(): String {
        val digits = RandomGenerator.digits(6)
        val builder = StringBuilder()

        Arrays.stream(digits).forEach { i -> builder.append(i) }

        return builder.toString()
    }
}

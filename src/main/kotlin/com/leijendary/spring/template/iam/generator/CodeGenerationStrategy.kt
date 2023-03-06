package com.leijendary.spring.template.iam.generator

import com.leijendary.spring.template.iam.entity.UserCredential

interface CodeGenerationStrategy {
    companion object {
        val OTP_STRATEGY = OtpCodeGenerationStrategy()
        val CHAR_STRATEGY = CharCodeGenerationStrategy()
        val UUID_STRATEGY = UuidCodeGenerationStrategy()

        fun fromField(field: String) = when (field) {
            UserCredential.Type.PHONE.value -> OTP_STRATEGY
            UserCredential.Type.EMAIL.value -> CHAR_STRATEGY
            else -> UUID_STRATEGY
        }
    }

    fun generate(): String
}

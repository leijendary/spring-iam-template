package com.leijendary.spring.template.iam.generator

import com.leijendary.spring.template.iam.entity.UserCredential.Type.EMAIL
import com.leijendary.spring.template.iam.entity.UserCredential.Type.PHONE

interface CodeGenerationStrategy {
    companion object {
        val OTP_STRATEGY = OtpCodeGenerationStrategy()
        val CHAR_STRATEGY = CharCodeGenerationStrategy()
        val UUID_STRATEGY = UUIDCodeGenerationStrategy()

        fun fromField(field: String) = when (field) {
            PHONE.value -> OTP_STRATEGY
            EMAIL.value -> CHAR_STRATEGY
            else -> UUID_STRATEGY
        }
    }

    fun generate(): String
}

package com.leijendary.spring.template.iam.generator

import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.entity.UserCredential.Type.EMAIL
import com.leijendary.spring.template.iam.entity.UserCredential.Type.PHONE

interface CodeGenerationStrategy {
    companion object {
        val OTP_STRATEGY = OtpCodeGenerationStrategy()
        val CHAR_STRATEGY = CharCodeGenerationStrategy()
        val UUID_STRATEGY = UUIDCodeGenerationStrategy()

        fun fromField(field: UserCredential.Type) = when (field) {
            PHONE -> OTP_STRATEGY
            EMAIL -> CHAR_STRATEGY
        }
    }

    fun generate(): String
}

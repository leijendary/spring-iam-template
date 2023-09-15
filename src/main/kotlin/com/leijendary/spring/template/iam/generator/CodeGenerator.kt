package com.leijendary.spring.template.iam.generator

import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.entity.UserCredential.Type.EMAIL
import com.leijendary.spring.template.iam.entity.UserCredential.Type.PHONE

interface CodeGenerator {
    companion object {
        val PIN_GENERATOR = PinCodeGenerator()
        val STRING_GENERATOR = StringCodeGenerator()
        val UUID_GENERATOR = UUIDCodeGenerator()

        fun fromField(field: UserCredential.Type) = when (field) {
            PHONE -> PIN_GENERATOR
            EMAIL -> STRING_GENERATOR
        }
    }

    fun generate(): String
}

package com.leijendary.spring.template.iam.core.validator

import com.leijendary.spring.template.iam.core.validator.annotation.EnumField
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class EnumFieldValidator : ConstraintValidator<EnumField, String?> {
    private val values = mutableListOf<String>()

    override fun initialize(constraintAnnotation: EnumField) {
        val constants = constraintAnnotation.enumClass.java.enumConstants

        for (value in constants) {
            values.add(value.toString().uppercase())
        }
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        return value
            ?.uppercase()
            ?.let { values.contains(it) }
            ?: true
    }
}
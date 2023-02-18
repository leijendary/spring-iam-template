package com.leijendary.spring.template.iam.validator

import com.leijendary.spring.template.iam.core.extension.isLong
import com.leijendary.spring.template.iam.validator.annotation.Phone
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class PhoneValidator : ConstraintValidator<Phone, String?> {
    companion object {
        // Yes, 5. https://en.wikipedia.org/wiki/Telephone_numbers_in_Saint_Helena_and_Tristan_da_Cunha
        const val MIN_LENGTH = 5
        const val MAX_LENGTH = 15
    }

    override fun initialize(constraintAnnotation: Phone) {
        super.initialize(constraintAnnotation)
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value.isNullOrBlank()) {
            return true
        }

        if (!value.isLong()) {
            return false
        }

        return value
            .trim { it <= ' ' }
            .length in MIN_LENGTH..MAX_LENGTH
    }
}

package com.leijendary.spring.iamtemplate.validator.v1;

import com.leijendary.spring.iamtemplate.validator.annotation.v1.MobileNumber;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.leijendary.spring.iamtemplate.util.NumberUtil.isLong;
import static java.util.Optional.ofNullable;

public class MobileNumberValidator implements ConstraintValidator<MobileNumber, String> {

    // Yes, 5. https://en.wikipedia.org/wiki/Telephone_numbers_in_Saint_Helena_and_Tristan_da_Cunha
    public static final int MIN_LENGTH = 5;
    public static final int MAX_LENGTH = 15;

    @Override
    public void initialize(final MobileNumber constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(final String value, ConstraintValidatorContext context) {
        return ofNullable(value)
                // Make this a valid mobile number if parseable by Long,
                // length is between MIN_LENGTH, and MAX_LENGTH
                .map(v -> v.isBlank() || isValidFormat(v))
                .orElse(true);
    }

    private boolean isValidFormat(String value) {
        return isLong(value)
                && value.trim().length() >= MIN_LENGTH
                && value.trim().length() <= MAX_LENGTH;
    }
}

package com.leijendary.spring.iamtemplate.validator.v1;

import com.leijendary.spring.iamtemplate.validator.annotation.v1.PreferredUsernameV1;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

import static com.leijendary.spring.iamtemplate.data.PreferredUsername.EMAIL_ADDRESS;
import static com.leijendary.spring.iamtemplate.data.PreferredUsername.MOBILE_NUMBER;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;

public class PreferredUsernameV1Validator implements ConstraintValidator<PreferredUsernameV1, String> {

    private final static List<String> VALID_VALUES = asList(EMAIL_ADDRESS, MOBILE_NUMBER);

    @Override
    public void initialize(final PreferredUsernameV1 constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        return ofNullable(value)
                .map(VALID_VALUES::contains)
                .orElse(false);
    }
}

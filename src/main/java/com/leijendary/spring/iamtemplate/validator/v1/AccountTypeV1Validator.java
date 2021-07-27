package com.leijendary.spring.iamtemplate.validator.v1;

import com.leijendary.spring.iamtemplate.validator.annotation.v1.AccountTypeV1;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

import static com.leijendary.spring.iamtemplate.data.AccountType.CUSTOMER;
import static com.leijendary.spring.iamtemplate.data.AccountType.PARTNER;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;

public class AccountTypeV1Validator implements ConstraintValidator<AccountTypeV1, String> {

    private final static List<String> VALID_TYPES = asList(CUSTOMER, PARTNER);

    @Override
    public void initialize(AccountTypeV1 constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        return ofNullable(value)
                .map(VALID_TYPES::contains)
                .orElse(false);
    }
}

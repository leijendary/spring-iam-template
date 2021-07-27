package com.leijendary.spring.iamtemplate.validator.annotation.v1;

import com.leijendary.spring.iamtemplate.validator.v1.MobileNumberValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = MobileNumberValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MobileNumber {

    String message() default "validation.mobileNumber.invalid";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}

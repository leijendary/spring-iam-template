package com.leijendary.spring.iamtemplate.validator.annotation.v1;

import com.leijendary.spring.iamtemplate.validator.v1.PreferredUsernameV1Validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PreferredUsernameV1Validator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PreferredUsernameV1 {

    String message() default "validation.user.preferredUsername.invalid";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}

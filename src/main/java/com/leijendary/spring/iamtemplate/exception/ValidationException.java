package com.leijendary.spring.iamtemplate.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.FieldError;

import java.util.List;

@AllArgsConstructor
public class ValidationException extends RuntimeException {

    @Getter
    private final List<FieldError> errors;
}

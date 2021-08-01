package com.leijendary.spring.iamtemplate.exception;

import lombok.Getter;

public class VerificationExpiredException extends RuntimeException {

    @Getter
    private final String field;

    public VerificationExpiredException(final String field) {
        this.field = field;
    }
}

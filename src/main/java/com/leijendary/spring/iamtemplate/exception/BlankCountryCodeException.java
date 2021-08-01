package com.leijendary.spring.iamtemplate.exception;

import lombok.Getter;

public class BlankCountryCodeException extends RuntimeException {

    @Getter
    private final String field;

    public BlankCountryCodeException(final String field) {
        this.field = field;
    }
}

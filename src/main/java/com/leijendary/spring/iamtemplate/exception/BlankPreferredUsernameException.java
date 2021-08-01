package com.leijendary.spring.iamtemplate.exception;

import lombok.Getter;

public class BlankPreferredUsernameException extends RuntimeException {

    @Getter
    private final String field;

    public BlankPreferredUsernameException(final String field) {
        this.field = field;
    }
}

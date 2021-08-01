package com.leijendary.spring.iamtemplate.exception;

import lombok.Getter;

public class InvalidPreferredUsernameException extends RuntimeException {

    @Getter
    private final String field;

    public InvalidPreferredUsernameException(final String field) {
        this.field = field;
    }
}

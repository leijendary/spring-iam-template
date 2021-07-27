package com.leijendary.spring.iamtemplate.exception;

import lombok.Getter;

public class InvalidRoleException extends RuntimeException {

    @Getter
    private final String field;

    @Getter
    private final long id;

    public InvalidRoleException(final String field, final long id) {
        this.field = field;
        this.id = id;
    }
}

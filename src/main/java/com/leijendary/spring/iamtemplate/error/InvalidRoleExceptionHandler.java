package com.leijendary.spring.iamtemplate.error;

import com.leijendary.spring.iamtemplate.data.response.ErrorResponse;
import com.leijendary.spring.iamtemplate.exception.InvalidRoleException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.leijendary.spring.iamtemplate.util.RequestContext.getLocale;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
@Order(3)
@RequiredArgsConstructor
public class InvalidRoleExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(InvalidRoleException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse catchInvalidRole(final InvalidRoleException exception) {
        final var code = "validation.role.invalid";
        final var message = messageSource.getMessage(code, new Object[] { exception.getId() }, getLocale());

        return ErrorResponse.builder()
                .addError(exception.getField(), code, message)
                .status(BAD_REQUEST)
                .build();
    }
}
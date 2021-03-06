package com.leijendary.spring.iamtemplate.error;

import com.leijendary.spring.iamtemplate.data.response.ErrorResponse;
import com.leijendary.spring.iamtemplate.exception.ResourceNotUniqueException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.leijendary.spring.iamtemplate.util.RequestContext.getLocale;
import static org.springframework.http.HttpStatus.CONFLICT;

@RestControllerAdvice
@Order(3)
@RequiredArgsConstructor
public class ResourceNotUniqueExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(ResourceNotUniqueException.class)
    @ResponseStatus(CONFLICT)
    public ErrorResponse catchResourceNotUnique(final ResourceNotUniqueException exception) {
        final var code = "validation.alreadyExists";
        final var message = messageSource.getMessage(code,
                new Object[] { exception.getField(), exception.getValue() }, getLocale());

        return ErrorResponse.builder()
                .addError(exception.getField(), code, message)
                .status(CONFLICT)
                .build();
    }
}
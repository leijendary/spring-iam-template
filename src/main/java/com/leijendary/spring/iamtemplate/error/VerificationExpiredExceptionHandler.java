package com.leijendary.spring.iamtemplate.error;

import com.leijendary.spring.iamtemplate.data.response.ErrorResponse;
import com.leijendary.spring.iamtemplate.exception.VerificationExpiredException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.leijendary.spring.iamtemplate.util.RequestContext.getLocale;
import static org.springframework.http.HttpStatus.GONE;

@RestControllerAdvice
@Order(3)
@RequiredArgsConstructor
public class VerificationExpiredExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(VerificationExpiredException.class)
    @ResponseStatus(GONE)
    public ErrorResponse catchVerificationExpired(final VerificationExpiredException exception) {
        final var code = "validation.verification.expired";
        final var message = messageSource.getMessage(code, new Object[0], getLocale());

        return ErrorResponse.builder()
                .addError(exception.getField(), code, message)
                .status(GONE)
                .build();
    }
}
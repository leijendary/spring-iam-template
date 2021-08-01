package com.leijendary.spring.iamtemplate.error;

import com.leijendary.spring.iamtemplate.data.response.ErrorResponse;
import com.leijendary.spring.iamtemplate.exception.BlankCountryCodeException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.leijendary.spring.iamtemplate.util.RequestContextUtil.getLocale;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
@Order(3)
@RequiredArgsConstructor
public class BlankCountryCodeExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(BlankCountryCodeException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse catchBlankCountryCode(final BlankCountryCodeException exception) {
        final var code = "validation.countryCode.blank";
        final var message = messageSource.getMessage(code, new Object[] { }, getLocale());

        return ErrorResponse.builder()
                .addError(exception.getField(), code, message)
                .status(BAD_REQUEST)
                .build();
    }
}
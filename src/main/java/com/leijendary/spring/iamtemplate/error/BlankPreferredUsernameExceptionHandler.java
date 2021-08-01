package com.leijendary.spring.iamtemplate.error;

import com.leijendary.spring.iamtemplate.data.response.ErrorResponse;
import com.leijendary.spring.iamtemplate.exception.BlankPreferredUsernameException;
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
public class BlankPreferredUsernameExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(BlankPreferredUsernameException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse catchBlankPreferredUsername(final BlankPreferredUsernameException exception) {
        final var code = "validation.preferredUsername.blank";
        final var message = messageSource.getMessage(code, new Object[] { exception.getField() }, getLocale());

        return ErrorResponse.builder()
                .addError(exception.getField(), code, message)
                .status(BAD_REQUEST)
                .build();
    }
}
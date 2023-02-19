package com.leijendary.spring.template.iam.core.error

import com.leijendary.spring.template.iam.core.exception.InvalidCredentialException
import com.leijendary.spring.template.iam.core.model.ErrorModel
import com.leijendary.spring.template.iam.core.util.RequestContext.locale
import org.springframework.context.MessageSource
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus.*
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Order(3)
class InvalidCredentialExceptionHandler(private val messageSource: MessageSource) {
    @ExceptionHandler(InvalidCredentialException::class)
    @ResponseStatus(UNAUTHORIZED)
    fun catchInvalidCredential(exception: InvalidCredentialException): List<ErrorModel> {
        val source = exception.source
        val code = "validation.credentials.invalid"
        val message = messageSource.getMessage(code, emptyArray(), locale)
        val error = ErrorModel(source, code, message)

        return listOf(error)
    }
}

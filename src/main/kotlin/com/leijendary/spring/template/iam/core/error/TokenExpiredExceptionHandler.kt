package com.leijendary.spring.template.iam.core.error

import com.leijendary.spring.template.iam.core.exception.TokenExpiredException
import com.leijendary.spring.template.iam.core.model.ErrorModel
import com.leijendary.spring.template.iam.core.util.RequestContext.locale
import org.springframework.context.MessageSource
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Order(3)
class TokenExpiredExceptionHandler(private val messageSource: MessageSource) {
    @ExceptionHandler(TokenExpiredException::class)
    @ResponseStatus(BAD_REQUEST)
    fun catchTokenExpired(exception: TokenExpiredException): List<ErrorModel> {
        val source = exception.source
        val code = "validation.token.expired"
        val message = messageSource.getMessage(code, emptyArray(), locale)
        val error = ErrorModel(source, code, message)

        return listOf(error)
    }
}

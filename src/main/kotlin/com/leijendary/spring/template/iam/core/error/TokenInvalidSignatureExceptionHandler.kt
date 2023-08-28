package com.leijendary.spring.template.iam.core.error

import com.leijendary.spring.template.iam.core.exception.TokenInvalidSignatureException
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
class TokenInvalidSignatureExceptionHandler(private val messageSource: MessageSource) {
    @ExceptionHandler(TokenInvalidSignatureException::class)
    @ResponseStatus(BAD_REQUEST)
    fun catchTokenInvalidSignature(exception: TokenInvalidSignatureException): List<ErrorModel> {
        val source = exception.source
        val code = "validation.token.invalid.signature"
        val message = messageSource.getMessage(code, emptyArray(), locale)
        val error = ErrorModel(source, code, message)

        return listOf(error)
    }
}

package com.leijendary.spring.template.iam.core.error

import com.leijendary.spring.template.iam.core.model.ErrorModel
import com.leijendary.spring.template.iam.core.util.RequestContext.locale
import org.springframework.context.MessageSource
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Order(4)
class HttpRequestMethodNotSupportedExceptionHandler(private val messageSource: MessageSource) {
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    @ResponseStatus(METHOD_NOT_ALLOWED)
    fun catchHttpRequestMethodNotSupported(exception: HttpRequestMethodNotSupportedException): List<ErrorModel> {
        val code = "error.method.notSupported"
        val arguments = arrayOf(exception.method, exception.supportedHttpMethods)
        val message = messageSource.getMessage(code, arguments, locale)
        val error = ErrorModel(mutableListOf("request", "method"), code, message)

        return listOf(error)
    }
}

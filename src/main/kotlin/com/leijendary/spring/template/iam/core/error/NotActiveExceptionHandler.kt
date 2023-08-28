package com.leijendary.spring.template.iam.core.error

import com.leijendary.spring.template.iam.core.exception.NotActiveException
import com.leijendary.spring.template.iam.core.model.ErrorModel
import com.leijendary.spring.template.iam.core.util.RequestContext.locale
import org.springframework.context.MessageSource
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Order(3)
class NotActiveExceptionHandler(private val messageSource: MessageSource) {
    @ExceptionHandler(NotActiveException::class)
    @ResponseStatus(FORBIDDEN)
    fun catchNotActive(exception: NotActiveException): List<ErrorModel> {
        val source = exception.source
        val code = exception.code
        val message = messageSource.getMessage(code, emptyArray(), locale)
        val error = ErrorModel(source, code, message)

        return listOf(error)
    }
}

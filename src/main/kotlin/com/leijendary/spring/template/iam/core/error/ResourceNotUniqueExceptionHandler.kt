package com.leijendary.spring.template.iam.core.error

import com.leijendary.spring.template.iam.core.exception.ResourceNotUniqueException
import com.leijendary.spring.template.iam.core.model.ErrorModel
import com.leijendary.spring.template.iam.core.util.RequestContext.locale
import org.springframework.context.MessageSource
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Order(3)
class ResourceNotUniqueExceptionHandler(private val messageSource: MessageSource) {
    @ExceptionHandler(ResourceNotUniqueException::class)
    @ResponseStatus(CONFLICT)
    fun catchResourceNotUnique(exception: ResourceNotUniqueException): List<ErrorModel> {
        val source = exception.source
        val code = "validation.alreadyExists"
        val arguments = arrayOf(source.joinToString("."), exception.value)
        val message = messageSource.getMessage(code, arguments, locale)
        val error = ErrorModel(source, code, message)

        return listOf(error)
    }
}

package com.leijendary.spring.template.iam.core.error

import com.leijendary.spring.template.iam.core.model.ErrorModel
import com.leijendary.spring.template.iam.core.util.RequestContext.locale
import org.springframework.context.MessageSource
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.validation.BindException
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Order(5)
class BindExceptionHandler(private val messageSource: MessageSource) {
    @ExceptionHandler(BindException::class)
    @ResponseStatus(BAD_REQUEST)
    fun catchBind(exception: BindException): List<ErrorModel> {
        return exception.allErrors.map { field ->
            val objectName = if (field is FieldError) field.field else field.objectName
            val source = listOf("param") + objectName.split(".", "[", "]")
                .filter { it.isNotBlank() }
                .map { it.toIntOrNull() ?: it }
            val code = field.defaultMessage ?: ""
            val arguments = field.arguments
            val message = code.let { messageSource.getMessage(it, arguments, code, locale) }

            ErrorModel(source, code, message)
        }
    }
}

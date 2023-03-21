package com.leijendary.spring.template.iam.core.error

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonMappingException.Reference
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.leijendary.spring.template.iam.core.model.ErrorModel
import com.leijendary.spring.template.iam.core.util.RequestContext.locale
import org.springframework.context.MessageSource
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

private const val PARENT = "body"

@RestControllerAdvice
@Order(2)
class HttpMessageNotReadableExceptionHandler(private val messageSource: MessageSource) {
    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(BAD_REQUEST)
    fun catchHttpMessageNotReadable(exception: HttpMessageNotReadableException) = errors(exception)

    private fun errors(exception: HttpMessageNotReadableException): List<ErrorModel> {
        val source = listOf(PARENT)
        val code = "error.badRequest"
        var message = exception.message ?: ""
        val error: ErrorModel

        if (message.startsWith("Required request body is missing")) {
            error = ErrorModel(source, code, message.split(":".toRegex()).toTypedArray()[0])

            return listOf(error)
        }

        error = when (val cause = exception.cause) {
            is InvalidFormatException -> error(cause)
            is JsonMappingException -> error(cause)
            else -> {
                message = exception.message?.replace("JSON decoding error: ", "") ?: ""

                ErrorModel(source, code, message)
            }
        }

        return listOf(error)
    }

    private fun error(exception: InvalidFormatException): ErrorModel {
        val source = source(exception.path)
        val code = "error.body.format.invalid"
        val field = source
            // Remove the parent field source
            .subList(1, source.size)
            .joinToString(".")
        val arguments = arrayOf(field, exception.value, exception.targetType.simpleName)
        val message = messageSource.getMessage(code, arguments, locale)

        return ErrorModel(source, code, message)
    }

    private fun error(exception: JsonMappingException): ErrorModel {
        val source = source(exception.path)
        val code = "error.body.format.invalid"
        val message = exception.originalMessage

        return ErrorModel(source, code, message)
    }

    private fun source(path: List<Reference>): List<Any> {
        val source = mutableListOf<Any>(PARENT)

        path.forEach {
            if (it.index >= 0) {
                source.add(it.index)
            }

            source.add(it.fieldName)
        }

        return source
    }
}

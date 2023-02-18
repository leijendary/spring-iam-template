package com.leijendary.spring.template.iam.core.exception

import org.springframework.http.HttpStatus

class StatusException(
    val source: List<String>,
    val code: String,
    val status: HttpStatus,
    val arguments: Array<Any> = emptyArray()
) : RuntimeException()

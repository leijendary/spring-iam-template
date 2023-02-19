package com.leijendary.spring.template.iam.core.exception

data class TokenExpiredException(val source: List<String>) : RuntimeException()

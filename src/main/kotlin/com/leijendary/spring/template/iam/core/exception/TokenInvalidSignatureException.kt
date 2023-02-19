package com.leijendary.spring.template.iam.core.exception

data class TokenInvalidSignatureException(val source: List<String>) : RuntimeException()

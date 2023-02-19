package com.leijendary.spring.template.iam.core.exception

data class InvalidCredentialException(
    val source: List<String> = listOf("data", "User", "credentials")
) : RuntimeException()

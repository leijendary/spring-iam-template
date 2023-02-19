package com.leijendary.spring.template.iam.core.exception

data class NotActiveException(val source: List<String>, val code: String) : RuntimeException()

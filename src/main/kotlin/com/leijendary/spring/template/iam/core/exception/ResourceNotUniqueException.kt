package com.leijendary.spring.template.iam.core.exception

class ResourceNotUniqueException(val source: List<String>, val value: String) : RuntimeException()

package com.leijendary.spring.template.iam.core.exception

class ResourceNotFoundException(val source: List<Any>, val identifier: Any) : RuntimeException()

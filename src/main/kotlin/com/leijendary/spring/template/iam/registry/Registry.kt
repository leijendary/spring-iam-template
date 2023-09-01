package com.leijendary.spring.template.iam.registry

interface Registry<T, S> {
    fun <R> using(type: T, function: S.() -> R?): R?
}

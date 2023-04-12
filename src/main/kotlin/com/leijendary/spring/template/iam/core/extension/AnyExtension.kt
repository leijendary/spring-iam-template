package com.leijendary.spring.template.iam.core.extension

import com.fasterxml.jackson.databind.ObjectMapper
import com.leijendary.spring.template.iam.core.util.SpringContext.Companion.getBean
import java.lang.reflect.Field

val mapper = getBean(ObjectMapper::class)

inline fun <reified T : Any> Any.toClass(): T {
    return mapper.convertValue(this, T::class.java)
}

fun Any.toJson(): String {
    return mapper.writeValueAsString(this)
}

fun Any.reflectField(property: String): Field {
    val field = try {
        this.javaClass.getDeclaredField(property)
    } catch (_: NoSuchFieldException) {
        this.javaClass.superclass.getDeclaredField(property)
    }
    field.isAccessible = true

    return field
}

fun Any.reflectGet(property: String): Any? = reflectField(property).get(this)

fun Any.reflectSet(property: String, value: Any?): Any? {
    val field = reflectField(property)
    field.set(this, value)

    return field.get(this)
}
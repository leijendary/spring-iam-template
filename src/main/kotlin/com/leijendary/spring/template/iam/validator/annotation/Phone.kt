package com.leijendary.spring.template.iam.validator.annotation

import com.leijendary.spring.template.iam.validator.PhoneValidator
import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.annotation.AnnotationTarget.PROPERTY_GETTER
import kotlin.reflect.KClass

@Constraint(validatedBy = [PhoneValidator::class])
@Target(PROPERTY_GETTER, FIELD)
@Retention(RUNTIME)
annotation class Phone(
    val message: String = "validation.phone.invalid",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

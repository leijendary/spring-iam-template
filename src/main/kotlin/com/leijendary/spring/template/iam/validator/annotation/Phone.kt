package com.leijendary.spring.template.iam.validator.annotation

import com.leijendary.spring.template.iam.validator.PhoneValidator
import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [PhoneValidator::class])
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Phone(
    val message: String = "validation.phone.invalid",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

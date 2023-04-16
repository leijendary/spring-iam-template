package com.leijendary.spring.template.iam.core.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.leijendary.spring.template.iam.core.config.properties.AuthProperties
import com.leijendary.spring.template.iam.core.util.SpringContext.Companion.getBean
import com.leijendary.spring.template.iam.event.VerificationEvent
import io.micrometer.tracing.Tracer
import org.springframework.transaction.PlatformTransactionManager

object BeanContainer {
    val authProperties by lazy { getBean(AuthProperties::class) }
    val objectMapper by lazy { getBean(ObjectMapper::class) }
    val tracer by lazy { getBean(Tracer::class) }
    val transactionManager by lazy { getBean(PlatformTransactionManager::class) }
    val verificationEvent by lazy { getBean(VerificationEvent::class) }
}
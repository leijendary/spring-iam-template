package com.leijendary.spring.template.iam.core.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.leijendary.spring.template.iam.core.config.properties.AuthProperties
import com.leijendary.spring.template.iam.core.util.SpringContext.Companion.getBean
import com.leijendary.spring.template.iam.event.VerificationEvent
import io.micrometer.tracing.Tracer
import org.springframework.transaction.PlatformTransactionManager

object BeanContainer {
    val AUTH_PROPERTIES = getBean(AuthProperties::class)
    val OBJECT_MAPPER = getBean(ObjectMapper::class)
    val TRACER = getBean(Tracer::class)
    val TRANSACTION_MANAGER = getBean(PlatformTransactionManager::class)
    val VERIFICATION_EVENT = getBean(VerificationEvent::class)
}
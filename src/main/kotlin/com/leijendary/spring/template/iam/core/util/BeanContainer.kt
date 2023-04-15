package com.leijendary.spring.template.iam.core.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.leijendary.spring.template.iam.core.config.properties.AuthProperties
import com.leijendary.spring.template.iam.core.util.SpringContext.Companion.getBean
import com.leijendary.spring.template.iam.event.VerificationEvent
import io.micrometer.tracing.Tracer
import org.springframework.transaction.PlatformTransactionManager

object BeanContainer {
    val AUTH_PROPERTIES by lazy { getBean(AuthProperties::class) }
    val OBJECT_MAPPER by lazy { getBean(ObjectMapper::class) }
    val TRACER by lazy { getBean(Tracer::class) }
    val TRANSACTION_MANAGER by lazy { getBean(PlatformTransactionManager::class) }
    val VERIFICATION_EVENT by lazy { getBean(VerificationEvent::class) }
}
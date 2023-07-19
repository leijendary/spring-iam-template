package com.leijendary.spring.template.iam.core.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.leijendary.spring.template.iam.core.config.properties.AuthProperties
import com.leijendary.spring.template.iam.core.storage.S3Storage
import com.leijendary.spring.template.iam.core.util.SpringContext.Companion.getBean
import io.micrometer.tracing.Tracer
import org.springframework.transaction.PlatformTransactionManager

object BeanContainer {
    val authProperties by lazy { getBean(AuthProperties::class) }
    val objectMapper by lazy { getBean(ObjectMapper::class) }
    val s3Storage by lazy { getBean(S3Storage::class) }
    val tracer by lazy { getBean(Tracer::class) }
    val transactionManager by lazy { getBean(PlatformTransactionManager::class) }
}

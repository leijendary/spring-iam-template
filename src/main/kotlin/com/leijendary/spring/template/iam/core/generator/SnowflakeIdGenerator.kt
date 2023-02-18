package com.leijendary.spring.template.iam.core.generator

import com.leijendary.spring.template.iam.core.worker.SnowflakeIdWorker
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator
import java.io.Serializable

class SnowflakeIdGenerator : IdentifierGenerator {
    companion object {
        const val STRATEGY = "com.leijendary.spring.template.iam.core.generator.SnowflakeIdGenerator"
        private val WORKER = SnowflakeIdWorker()
    }

    override fun generate(session: SharedSessionContractImplementor, any: Any): Serializable = WORKER.nextId()
}

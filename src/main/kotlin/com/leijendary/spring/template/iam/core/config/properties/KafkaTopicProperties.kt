package com.leijendary.spring.template.iam.core.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("spring.kafka.topic")
class KafkaTopicProperties : HashMap<String, String>() {
    companion object {
        const val TOPIC_SAMPLE_CREATE = "sample.create"
        const val TOPIC_SAMPLE_UPDATE = "sample.update"
        const val TOPIC_SAMPLE_DELETE = "sample.delete"
    }

    val sampleCreate: String
        get() = get(TOPIC_SAMPLE_CREATE)!!

    val sampleUpdate: String
        get() = get(TOPIC_SAMPLE_UPDATE)!!

    val sampleDelete: String
        get() = get(TOPIC_SAMPLE_DELETE)!!
}

package com.leijendary.spring.template.iam.core.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("spring.kafka.topic")
class KafkaTopicProperties : HashMap<String, String>()

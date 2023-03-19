package com.leijendary.spring.template.iam.message

import com.leijendary.spring.template.iam.core.config.properties.KafkaTopicProperties
import com.leijendary.spring.template.iam.core.extension.AnyUtil.toJson
import com.leijendary.spring.template.iam.message.Topic.USER_DEVICE_CREATED
import com.leijendary.spring.template.iam.message.Topic.USER_DEVICE_DELETED
import com.leijendary.spring.template.iam.model.UserDeviceMessage
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class UserDeviceMessageProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    kafkaTopicProperties: KafkaTopicProperties
) {
    private val created = kafkaTopicProperties[USER_DEVICE_CREATED]!!
    private val deleted = kafkaTopicProperties[USER_DEVICE_DELETED]!!

    fun created(userDeviceMessage: UserDeviceMessage) {
        kafkaTemplate.send(created, userDeviceMessage.toJson())
    }

    fun deleted(userDeviceMessage: UserDeviceMessage) {
        kafkaTemplate.send(deleted, userDeviceMessage.toJson())
    }
}
package com.leijendary.spring.template.iam.message

import com.leijendary.spring.template.iam.api.v1.mapper.UserAddressMapper
import com.leijendary.spring.template.iam.api.v1.mapper.UserMapper
import com.leijendary.spring.template.iam.core.config.properties.KafkaTopicProperties
import com.leijendary.spring.template.iam.core.extension.toJson
import com.leijendary.spring.template.iam.entity.User
import com.leijendary.spring.template.iam.entity.UserAddress
import com.leijendary.spring.template.iam.message.Topic.USER_ADDRESS_UPDATED
import com.leijendary.spring.template.iam.message.Topic.USER_EMAIL_UPDATED
import com.leijendary.spring.template.iam.message.Topic.USER_PHONE_UPDATED
import com.leijendary.spring.template.iam.message.Topic.USER_PROFILE_UPDATED
import com.leijendary.spring.template.iam.message.Topic.USER_REGISTERED
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component

@Component
class UserMessageProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val kafkaTopicProperties: KafkaTopicProperties,
) {
    @Retryable
    fun registered(user: User) {
        val userMessage = UserMapper.INSTANCE.toMessage(user)
        val topic = kafkaTopicProperties.nameOf(USER_REGISTERED)

        kafkaTemplate.send(topic, userMessage.toJson())
    }

    @Retryable
    fun profileUpdated(user: User) {
        val userMessage = UserMapper.INSTANCE.toMessage(user)
        val topic = kafkaTopicProperties.nameOf(USER_PROFILE_UPDATED)

        kafkaTemplate.send(topic, userMessage.toJson())
    }

    @Retryable
    fun emailUpdated(user: User) {
        val userMessage = UserMapper.INSTANCE.toEmailMessage(user)
        val topic = kafkaTopicProperties.nameOf(USER_EMAIL_UPDATED)

        kafkaTemplate.send(topic, userMessage.toJson())
    }

    @Retryable
    fun phoneUpdated(user: User) {
        val userMessage = UserMapper.INSTANCE.toPhoneMessage(user)
        val topic = kafkaTopicProperties.nameOf(USER_PHONE_UPDATED)

        kafkaTemplate.send(topic, userMessage.toJson())
    }

    @Retryable
    fun addressUpdated(userAddress: UserAddress) {
        val userMessage = UserAddressMapper.INSTANCE.toMessage(userAddress)
        val topic = kafkaTopicProperties.nameOf(USER_ADDRESS_UPDATED)

        kafkaTemplate.send(topic, userMessage.toJson())
    }
}

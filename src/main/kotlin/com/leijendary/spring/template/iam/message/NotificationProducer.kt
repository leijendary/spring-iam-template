package com.leijendary.spring.template.iam.message

import com.leijendary.spring.template.iam.core.config.properties.KafkaTopicProperties
import com.leijendary.spring.template.iam.core.extension.toJson
import com.leijendary.spring.template.iam.message.Topic.NOTIFICATION_EMAIL
import com.leijendary.spring.template.iam.message.Topic.NOTIFICATION_PUSH
import com.leijendary.spring.template.iam.message.Topic.NOTIFICATION_SMS
import com.leijendary.spring.template.iam.model.EmailMessage
import com.leijendary.spring.template.iam.model.PushMessage
import com.leijendary.spring.template.iam.model.SmsMessage
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component

@Component
class NotificationProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val kafkaTopicProperties: KafkaTopicProperties
) {
    @Retryable
    fun email(emailMessage: EmailMessage) {
        val topic = kafkaTopicProperties.nameOf(NOTIFICATION_EMAIL)

        kafkaTemplate.send(topic, emailMessage.toJson())
    }

    @Retryable
    fun push(pushMessage: PushMessage) {
        val topic = kafkaTopicProperties.nameOf(NOTIFICATION_PUSH)

        kafkaTemplate.send(topic, pushMessage.toJson())
    }

    @Retryable
    fun sms(smsMessage: SmsMessage) {
        val topic = kafkaTopicProperties.nameOf(NOTIFICATION_SMS)

        kafkaTemplate.send(topic, smsMessage.toJson())
    }
}

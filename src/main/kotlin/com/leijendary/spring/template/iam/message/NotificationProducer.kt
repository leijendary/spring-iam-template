package com.leijendary.spring.template.iam.message

import com.leijendary.spring.template.iam.core.config.properties.KafkaTopicProperties
import com.leijendary.spring.template.iam.core.extension.AnyUtil.toJson
import com.leijendary.spring.template.iam.message.Topic.NOTIFICATION_EMAIL
import com.leijendary.spring.template.iam.message.Topic.NOTIFICATION_PUSH
import com.leijendary.spring.template.iam.message.Topic.NOTIFICATION_SMS
import com.leijendary.spring.template.iam.model.EmailMessage
import com.leijendary.spring.template.iam.model.PushMessage
import com.leijendary.spring.template.iam.model.SmsMessage
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class NotificationProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    kafkaTopicProperties: KafkaTopicProperties
) {
    private val email = kafkaTopicProperties[NOTIFICATION_EMAIL]!!
    private val push = kafkaTopicProperties[NOTIFICATION_PUSH]!!
    private val sms = kafkaTopicProperties[NOTIFICATION_SMS]!!

    fun email(emailMessage: EmailMessage) {
        kafkaTemplate.send(email, emailMessage.toJson())
    }

    fun push(pushMessage: PushMessage) {
        kafkaTemplate.send(push, pushMessage.toJson())
    }

    fun sms(smsMessage: SmsMessage) {
        kafkaTemplate.send(sms, smsMessage.toJson())
    }
}
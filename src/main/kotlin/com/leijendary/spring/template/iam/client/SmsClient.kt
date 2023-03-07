package com.leijendary.spring.template.iam.client

import io.awspring.cloud.sns.sms.SnsSmsTemplate
import org.springframework.stereotype.Component

@Component
class SmsClient(private val snsSmsTemplate: SnsSmsTemplate) {
    fun send(to: String, message: String) = snsSmsTemplate.send(to, message)
}

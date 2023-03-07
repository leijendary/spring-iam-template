package com.leijendary.spring.template.iam.client

import com.leijendary.spring.template.iam.core.config.properties.InfoProperties
import com.leijendary.spring.template.iam.core.util.RequestContext.locale
import com.leijendary.spring.template.iam.model.NotificationTemplate
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine

@Component
class MailClient(
    private val infoProperties: InfoProperties,
    private val javaMailSender: JavaMailSender,
    private val templateEngine: SpringTemplateEngine
) {
    fun send(notificationTemplate: NotificationTemplate) {
        val context = Context(locale, notificationTemplate.parameters)
        val content = templateEngine.process(notificationTemplate.name, context)
        val message = javaMailSender.createMimeMessage()

        MimeMessageHelper(message, true).apply {
            setTo(notificationTemplate.to!!)
            setFrom(infoProperties.api.contact!!.email)
            setSubject(notificationTemplate.subject!!)
            setText(content, true)
        }

        javaMailSender.send(message)
    }
}

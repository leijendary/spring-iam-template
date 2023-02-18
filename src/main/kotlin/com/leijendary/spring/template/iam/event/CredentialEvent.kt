package com.leijendary.spring.template.iam.event

import com.leijendary.spring.template.iam.core.config.properties.InfoProperties
import com.leijendary.spring.template.iam.core.config.properties.VerificationProperties
import com.leijendary.spring.template.iam.core.util.RequestContext.locale
import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.entity.Verification
import com.leijendary.spring.template.iam.generator.HtmlGenerator
import com.leijendary.spring.template.iam.util.VerificationType
import io.awspring.cloud.sns.sms.SnsSmsTemplate
import org.springframework.context.MessageSource
import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component

@Component
class CredentialEvent(
    private val htmlGenerator: HtmlGenerator,
    private val infoProperties: InfoProperties,
    private val mailSender: MailSender,
    private val messageSource: MessageSource,
    private val snsSmsTemplate: SnsSmsTemplate,
    private val verificationProperties: VerificationProperties
) {
    @Retryable
    fun verify(verification: Verification) {
        val field = verification.field
        val type = verification.type
        val code = verification.code
        val user = verification.user!!
        val to = user.getUsername(field)

        when (field) {
            UserCredential.Type.EMAIL.value -> email(type, user.fullName, code, to)
            UserCredential.Type.PHONE.value -> phone(type, code, to)
        }
    }

    private fun email(type: String, fullName: String, code: String, to: String) {
        val (template, config) = when (type) {
            VerificationType.VERIFICATION -> "register.verify" to verificationProperties.register
            VerificationType.RESET_PASSWORD -> "reset-password.verify" to verificationProperties.resetPassword
            else -> return
        }
        val params = mapOf(
            "name" to fullName,
            "url" to config.url!!.replace("{code}", code)
        )
        val content = htmlGenerator.parse(template, params)
        val message = SimpleMailMessage().apply {
            setTo(to)
            from = infoProperties.api.contact!!.email
            subject = config.subject
            text = content
        }

        mailSender.send(message)
    }

    private fun phone(type: String, code: String, to: String) {
        val key = when (type) {
            VerificationType.VERIFICATION -> "notification.verification.sms"
            VerificationType.RESET_PASSWORD -> "notification.resetPassword.sms"
            else -> return
        }
        val message = messageSource.getMessage(key, arrayOf(code), locale)

        snsSmsTemplate.send(to, message)
    }
}

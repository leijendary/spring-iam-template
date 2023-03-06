package com.leijendary.spring.template.iam.event

import com.leijendary.spring.template.iam.core.config.properties.InfoProperties
import com.leijendary.spring.template.iam.core.config.properties.VerificationProperties
import com.leijendary.spring.template.iam.core.util.RequestContext.locale
import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.entity.Verification
import com.leijendary.spring.template.iam.generator.HtmlGenerator
import io.awspring.cloud.sns.sms.SnsSmsTemplate
import org.springframework.context.MessageSource
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component

// TODO: remove this.
@Component
class CredentialEvent(
    private val htmlGenerator: HtmlGenerator,
    private val infoProperties: InfoProperties,
    private val javaMailSender: JavaMailSender,
    private val messageSource: MessageSource,
    private val snsSmsTemplate: SnsSmsTemplate,
    private val verificationProperties: VerificationProperties
) {
    @Retryable
    fun verify(verification: Verification) {
        val field = verification.field
        val type = verification.type.let { Verification.Type.valueOf(it) }
        val code = verification.code
        val user = verification.user
        val to = user.getUsername(field!!)

        when (field) {
            UserCredential.Type.EMAIL.value -> email(type, user.fullName, code, to)
            UserCredential.Type.PHONE.value -> phone(type, code, to)
        }
    }

    private fun email(type: Verification.Type, fullName: String, code: String, to: String) {
        val (template, config) = when (type) {
            Verification.Type.EMAIL_VERIFY -> "email.verify" to verificationProperties.email

            Verification.Type.VERIFICATION,
            Verification.Type.REGISTRATION -> "register.verify" to verificationProperties.register

            Verification.Type.PASSWORD_CHANGE_VERIFY -> {
                "password-change.verify" to verificationProperties.password.change
            }

            Verification.Type.PASSWORD_RESET -> "password-reset.verify" to verificationProperties.password.reset
            else -> return
        }
        val params = mapOf(
            "name" to fullName,
            "code" to code
        )
        val content = htmlGenerator.parse(template, params)
        val message = javaMailSender.createMimeMessage()

        MimeMessageHelper(message, true).apply {
            setTo(to)
            setFrom(infoProperties.api.contact!!.email)
            setSubject(config.subject!!)
            setText(content, true)
        }

        javaMailSender.send(message)
    }

    private fun phone(type: Verification.Type, code: String, to: String) {
        val key = when (type) {
            Verification.Type.PHONE_VERIFY,
            Verification.Type.VERIFICATION,
            Verification.Type.REGISTRATION -> "notification.verification.sms"

            Verification.Type.PASSWORD_CHANGE_VERIFY -> "notification.password.change.sms"
            Verification.Type.PASSWORD_RESET -> "notification.password.reset.sms"
            else -> return
        }
        val message = messageSource.getMessage(key, arrayOf(code), locale)

        snsSmsTemplate.send(to, message)
    }
}

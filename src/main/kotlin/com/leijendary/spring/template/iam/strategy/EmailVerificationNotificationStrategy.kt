package com.leijendary.spring.template.iam.strategy

import com.leijendary.spring.template.iam.core.config.properties.VerificationProperties
import com.leijendary.spring.template.iam.core.util.RequestContext.locale
import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.entity.UserCredential.Type.EMAIL
import com.leijendary.spring.template.iam.entity.Verification
import com.leijendary.spring.template.iam.entity.Verification.Type.*
import com.leijendary.spring.template.iam.message.NotificationProducer
import com.leijendary.spring.template.iam.model.EmailMessage
import com.leijendary.spring.template.iam.model.NotificationTemplate
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine

@Component
class EmailVerificationNotificationStrategy(
    private val notificationProducer: NotificationProducer,
    private val templateEngine: SpringTemplateEngine,
    private val verificationProperties: VerificationProperties
) : VerificationNotificationStrategy {
    override val field: UserCredential.Type
        get() = EMAIL

    override fun template(code: String, type: Verification.Type) = when (type) {
        REGISTRATION -> NotificationTemplate(
            "register.verify",
            codeParameter(code),
            verificationProperties.register.subject
        )

        EMAIL_CHANGE -> NotificationTemplate(
            "email.change",
            codeParameter(code),
            verificationProperties.email.subject
        )

        PASSWORD_RESET -> NotificationTemplate(
            "password.reset",
            codeParameter(code),
            verificationProperties.password.reset.subject
        )

        PASSWORD_NOMINATE -> NotificationTemplate(
            "password.nominate",
            linkParameter(code),
            verificationProperties.password.nominate.subject
        )

        else -> null
    }

    override fun send(verification: Verification) {
        val code = verification.code
        val type = verification.type
        val template = template(code, type) ?: return
        val subject = template.subject!!
        val context = Context(locale, template.parameters)
        val content = templateEngine.process(template.name, context)
        val value = verification.value!!
        val emailMessage = EmailMessage(value, subject, content, null)

        notificationProducer.email(emailMessage)
    }

    private fun codeParameter(code: String) = mapOf("code" to code)

    private fun linkParameter(code: String): Map<String, String> {
        val link = verificationProperties.password.nominate.url!!.let {
            UriComponentsBuilder
                .fromUriString(it)
                .replaceQueryParam("code", code)
                .build()
                .toUriString()
        }

        return mapOf("link" to link)
    }
}

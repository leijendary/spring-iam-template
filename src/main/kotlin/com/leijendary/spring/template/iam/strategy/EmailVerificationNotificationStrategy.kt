package com.leijendary.spring.template.iam.strategy

import com.leijendary.spring.template.iam.core.config.properties.VerificationProperties
import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.entity.UserCredential.Type.EMAIL
import com.leijendary.spring.template.iam.entity.Verification
import com.leijendary.spring.template.iam.entity.Verification.Type.*
import com.leijendary.spring.template.iam.message.NotificationMessageProducer
import com.leijendary.spring.template.iam.model.NotificationTemplate
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder

@Component
class EmailVerificationNotificationStrategy(
    private val notificationMessageProducer: NotificationMessageProducer,
    private val verificationProperties: VerificationProperties
) : VerificationNotificationStrategy {
    override val field: UserCredential.Type
        get() = EMAIL

    override fun template(code: String, type: Verification.Type) = when (type) {
        REGISTRATION -> NotificationTemplate(
            verificationProperties.register.templateId,
            codeParameter(code),
        )

        EMAIL_CHANGE -> NotificationTemplate(
            verificationProperties.email.templateId,
            codeParameter(code),
        )

        PASSWORD_RESET -> NotificationTemplate(
            verificationProperties.password.reset.templateId,
            codeParameter(code),
        )

        PASSWORD_NOMINATE -> NotificationTemplate(
            verificationProperties.password.nominate.templateId,
            linkParameter(code),
        )

        else -> null
    }

    override fun send(verification: Verification) {
        val template = template(verification.code, verification.type) ?: return

        notificationMessageProducer.email(verification.value!!, template.name, template.parameters)
    }

    private fun codeParameter(code: String) = mapOf("code" to code)

    private fun linkParameter(code: String): Map<String, String> {
        val link = UriComponentsBuilder
            .fromUriString(verificationProperties.password.nominate.url)
            .replaceQueryParam("code", code)
            .build()
            .toUriString()

        return mapOf("link" to link)
    }
}

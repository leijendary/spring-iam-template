package com.leijendary.spring.template.iam.strategy

import com.leijendary.spring.template.iam.client.MailClient
import com.leijendary.spring.template.iam.core.config.properties.VerificationProperties
import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.entity.UserCredential.Type.EMAIL
import com.leijendary.spring.template.iam.entity.Verification
import com.leijendary.spring.template.iam.entity.Verification.Type.*
import com.leijendary.spring.template.iam.model.NotificationTemplate
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder

@Component
class EmailVerificationNotificationStrategy(
    private val mailClient: MailClient,
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
        val value = verification.value!!
        val code = verification.code
        val type = verification.type.let { Verification.Type.from(it) }
        val template = template(code, type)?.apply { this.to = value } ?: return

        mailClient.send(template)
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

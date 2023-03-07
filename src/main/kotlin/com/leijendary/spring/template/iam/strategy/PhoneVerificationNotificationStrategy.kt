package com.leijendary.spring.template.iam.strategy

import com.leijendary.spring.template.iam.client.SmsClient
import com.leijendary.spring.template.iam.core.util.RequestContext.locale
import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.entity.Verification
import com.leijendary.spring.template.iam.entity.Verification.Type.*
import com.leijendary.spring.template.iam.model.NotificationTemplate
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component

@Component
class PhoneVerificationNotificationStrategy(
    private val messageSource: MessageSource,
    private val smsClient: SmsClient
) : VerificationNotificationStrategy {
    override val field: UserCredential.Type
        get() = UserCredential.Type.PHONE

    override fun template(code: String, type: Verification.Type): NotificationTemplate? {
        val parameters = mapOf("code" to code)
        val name = when (type) {
            REGISTRATION -> "notification.sms.registration"
            PHONE_CHANGE -> "notification.sms.verification"
            PASSWORD_RESET -> "notification.sms.password.reset"
            else -> null
        } ?: return null

        return NotificationTemplate(name, parameters)
    }

    override fun send(verification: Verification) {
        val value = verification.value!!
        val code = verification.code
        val type = verification.type.let { Verification.Type.from(it) }
        val template = template(code, type) ?: return
        val args = template.parameters
            .map { it.value }
            .toTypedArray()
        val message = messageSource.getMessage(template.name, args, locale)

        smsClient.send(value, message)
    }
}

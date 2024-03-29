package com.leijendary.spring.template.iam.handler

import com.leijendary.spring.template.iam.core.util.RequestContext.locale
import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.entity.UserCredential.Type.PHONE
import com.leijendary.spring.template.iam.entity.Verification
import com.leijendary.spring.template.iam.entity.Verification.Type.*
import com.leijendary.spring.template.iam.message.NotificationMessageProducer
import com.leijendary.spring.template.iam.model.NotificationTemplate
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component

@Component
class PhoneVerificationNotificationHandler(
    private val messageSource: MessageSource,
    private val notificationMessageProducer: NotificationMessageProducer
) : VerificationNotificationHandler {
    override val field: UserCredential.Type
        get() = PHONE

    override fun template(code: String, type: Verification.Type): NotificationTemplate? {
        val parameters = mapOf("code" to code)
        val name = when (type) {
            REGISTRATION -> "notification.sms.registration"
            PHONE_CHANGE -> "notification.sms.verification"
            PASSWORD_RESET -> "notification.sms.password.reset"
            else -> return null
        }

        return NotificationTemplate(name, parameters)
    }

    override fun send(verification: Verification) {
        val template = template(verification.code, verification.type) ?: return
        val args = template.parameters.map { it.value }.toTypedArray()
        val to = verification.value!!.replace(" ", "")
        val message = messageSource.getMessage(template.name, args, locale)

        notificationMessageProducer.sms(to, message)
    }
}

package com.leijendary.spring.template.iam.registry

import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.handler.VerificationNotificationHandler
import org.springframework.stereotype.Component

@Component
class VerificationNotificationRegistry(notificationStrategies: List<VerificationNotificationHandler>) :
    Registry<UserCredential.Type, VerificationNotificationHandler> {
    private val strategy = notificationStrategies.associateBy { it.field }

    override fun <R> using(type: UserCredential.Type, function: VerificationNotificationHandler.() -> R?): R? {
        return strategy[type]?.run(function)
    }
}

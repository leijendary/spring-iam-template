package com.leijendary.spring.template.iam.registry

import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.strategy.VerificationNotificationStrategy
import org.springframework.stereotype.Component

@Component
class VerificationNotificationRegistry(notificationStrategies: List<VerificationNotificationStrategy>) :
    Registry<UserCredential.Type, VerificationNotificationStrategy> {
    private val strategy = notificationStrategies.associateBy { it.field }

    override fun <R> using(type: UserCredential.Type, function: VerificationNotificationStrategy.() -> R?): R? {
        return strategy[type]?.run(function)
    }
}

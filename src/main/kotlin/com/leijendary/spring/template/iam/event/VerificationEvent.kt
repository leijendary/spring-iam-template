package com.leijendary.spring.template.iam.event

import com.leijendary.spring.template.iam.entity.Verification
import com.leijendary.spring.template.iam.strategy.VerificationNotificationStrategy
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component

@Component
class VerificationEvent(notificationStrategies: List<VerificationNotificationStrategy>) {
    private val strategy = notificationStrategies.associateBy { it.field }

    @Retryable
    fun notify(verification: Verification) {
        val field = verification.field
        val value = verification.value

        if (field == null || value.isNullOrBlank()) {
            return
        }

        strategy[field]?.send(verification)
    }
}

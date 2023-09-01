package com.leijendary.spring.template.iam.event

import com.leijendary.spring.template.iam.entity.Verification
import com.leijendary.spring.template.iam.registry.VerificationNotificationRegistry
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component

@Component
class VerificationEvent(private val verificationNotificationRegistry: VerificationNotificationRegistry) {
    @Retryable
    fun notify(verification: Verification) {
        val field = verification.field
        val value = verification.value

        if (field == null || value.isNullOrBlank()) {
            return
        }

        verificationNotificationRegistry.using(field) { send(verification) }
    }
}

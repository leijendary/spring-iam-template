package com.leijendary.spring.template.iam.entity.listener

import com.leijendary.spring.template.iam.core.util.SpringContext.Companion.getBean
import com.leijendary.spring.template.iam.entity.Verification
import com.leijendary.spring.template.iam.event.VerificationNotificationEvent
import jakarta.persistence.PostPersist

private val verificationNotificationEvent = getBean(VerificationNotificationEvent::class)

class VerificationListener {
    @PostPersist
    fun onCreate(verification: Verification) {
        verificationNotificationEvent.notify(verification)
    }
}

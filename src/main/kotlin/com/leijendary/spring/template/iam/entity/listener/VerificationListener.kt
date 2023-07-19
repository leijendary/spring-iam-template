package com.leijendary.spring.template.iam.entity.listener

import com.leijendary.spring.template.iam.core.util.SpringContext.Companion.getBean
import com.leijendary.spring.template.iam.entity.Verification
import com.leijendary.spring.template.iam.event.VerificationEvent
import jakarta.persistence.PostPersist

class VerificationListener {
    private val verificationEvent by lazy { getBean(VerificationEvent::class) }

    @PostPersist
    fun onCreate(verification: Verification) {
        verificationEvent.notify(verification)
    }
}

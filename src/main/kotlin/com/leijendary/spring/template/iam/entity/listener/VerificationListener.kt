package com.leijendary.spring.template.iam.entity.listener

import com.leijendary.spring.template.iam.core.util.BeanContainer.verificationEvent
import com.leijendary.spring.template.iam.entity.Verification
import jakarta.persistence.PostPersist

class VerificationListener {
    @PostPersist
    fun onCreate(verification: Verification) {
        verificationEvent.notify(verification)
    }
}

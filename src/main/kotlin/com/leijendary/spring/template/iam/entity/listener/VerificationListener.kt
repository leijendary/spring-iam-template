package com.leijendary.spring.template.iam.entity.listener

import com.leijendary.spring.template.iam.core.util.BeanContainer.VERIFICATION_EVENT
import com.leijendary.spring.template.iam.entity.Verification
import jakarta.persistence.PostPersist

class VerificationListener {
    @PostPersist
    fun onCreate(verification: Verification) {
        VERIFICATION_EVENT.notify(verification)
    }
}

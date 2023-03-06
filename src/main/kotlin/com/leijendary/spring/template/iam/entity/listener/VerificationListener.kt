package com.leijendary.spring.template.iam.entity.listener

import com.leijendary.spring.template.iam.entity.Verification
import jakarta.persistence.PostPersist

class VerificationListener {
    @PostPersist
    fun onCreate(verification: Verification) {
        println("On Create $verification")
    }
}

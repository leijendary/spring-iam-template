package com.leijendary.spring.template.iam.handler

import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.entity.Verification
import com.leijendary.spring.template.iam.model.NotificationTemplate

interface VerificationNotificationHandler {
    val field: UserCredential.Type

    fun template(code: String, type: Verification.Type): NotificationTemplate?

    fun send(verification: Verification)
}
